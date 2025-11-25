package com.isa.solution.apiclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isa.solution.exception.ApiConnectionException;
import com.isa.solution.exception.ApiResponseException;
import com.isa.solution.exception.DragonsApiException;
import com.isa.solution.exception.GameNotFoundException;
import com.isa.solution.exception.InsufficientResourcesException;
import com.isa.solution.exception.InvalidGameStateException;
import com.isa.solution.exception.InvalidRequestException;
import com.isa.solution.exception.MissionNotFoundException;
import com.isa.solution.model.Game;
import com.isa.solution.model.Message;
import com.isa.solution.model.PurchaseResponse;
import com.isa.solution.model.ShopItem;
import com.isa.solution.model.SolveResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Service
public class DragonsApiClientImpl implements DragonsApiClient {

    private static final Logger log = LoggerFactory.getLogger(DragonsApiClientImpl.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public DragonsApiClientImpl(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${dragons.api.base-url:https://dragonsofmugloar.com/api/v2}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
    }

    @Override
    public Game startGame() {
        String endpoint = buildUrl("game", "start");
        return executeApiCall("Start new game", endpoint, () ->
                postForObject(endpoint, null, Game.class)
        );
    }

    @Override
    public List<Message> getAllMissions(String gameId) {
        validateId("gameId", gameId);
        String endpoint = buildUrl(gameId, "messages");
        return executeGetList(
                "Get all missions",
                endpoint,
                new TypeReference<>() {},
                gameId
        );
    }

    @Override
    public SolveResponse solveMission(String gameId, String missionId) {
        validateId("gameId", gameId);
        validateId("missionId", missionId);
        String endpoint = buildUrl(gameId, "solve", missionId);
        String action = String.format("Solve mission %s", missionId);

        return executeApiCall(action, endpoint, () -> {
            try {
                return postForObject(endpoint, null, SolveResponse.class);
            } catch (HttpClientErrorException.NotFound e) {
                throw handleNotFound(e, gameId, missionId);
            } catch (HttpClientErrorException.BadRequest e) {
                throw new InvalidGameStateException(gameId, missionId, "Cannot solve mission in current state");
            }
        });
    }

    @Override
    public List<ShopItem> getShopItems(String gameId) {
        validateId("gameId", gameId);
        String endpoint = buildUrl(gameId, "shop");
        return executeGetList(
                "Get shop items",
                endpoint,
                new TypeReference<>() {},
                gameId
        );
    }

    @Override
    public PurchaseResponse purchaseItem(String gameId, String itemId) {
        validateId("gameId", gameId);
        validateId("itemId", itemId);
        String endpoint = buildUrl(gameId, "shop", "buy", itemId);
        String action = String.format("Purchase item %s", itemId);

        return executeApiCall(action, endpoint, () -> {
            try {
                return postForObject(endpoint, null, PurchaseResponse.class);
            } catch (HttpClientErrorException.NotFound e) {
                throw handleNotFound(e, gameId, null);
            } catch (HttpClientErrorException.BadRequest e) {
                throw handlePurchaseBadRequest(e);
            }
        });
    }

    private <T> T executeGetList(String action, String endpoint, TypeReference<T> typeRef, String gameId) {
        return executeApiCall(action, endpoint, () -> {
            try {
                String json = getForString(endpoint);
                return objectMapper.readValue(json, typeRef);
            } catch (HttpClientErrorException.NotFound | JsonProcessingException e) {
                throw new GameNotFoundException(gameId, e);
            }
        });
    }

    private <T> T executeApiCall(String action, String endpoint, Supplier<T> callable) {
        try {
            log.info("{} at endpoint: {}", action, endpoint);
            T result = callable.get();
            log.info("Successfully completed action: {}", action);
            return result;
        } catch (ResourceAccessException e) {
            throw new ApiConnectionException(endpoint, "Connection failed", e);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String body = getResponseBody(e);
            log.error("HTTP error during '{}': {} - {}", action, e.getStatusCode(), snippet(body));
            throw new ApiResponseException(e.getStatusCode().value(), "Failed to " + action, body, e);
        } catch (DragonsApiException e) {
            throw e;
        } catch (Exception e) {
            throw new DragonsApiException("Unexpected error during " + action, e);
        }
    }

    private <T> T postForObject(String endpoint, Object request, Class<T> responseType) {
        return Objects.requireNonNull(restTemplate.postForEntity(endpoint, request, responseType).getBody());
    }

    private String getForString(String endpoint) {
        String body = restTemplate.getForEntity(endpoint, String.class).getBody();
        if (body != null && (body.trim().toLowerCase().startsWith("<!doctype") || body.trim().toLowerCase().startsWith("<html"))) {
            throw new ApiResponseException(400, "Server returned HTML instead of JSON", body, null);
        }
        return body;
    }

    private void validateId(String fieldName, String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidRequestException(fieldName, id, "ID cannot be null or empty");
        }
    }

    private DragonsApiException handleNotFound(HttpClientErrorException e, String gameId, String resourceId) {
        String body = getResponseBody(e);
        if (resourceId != null && body != null && (body.contains("mission") || body.contains("ad"))) {
            return new MissionNotFoundException(gameId, resourceId, e);
        }
        if (body != null && body.contains("item")) {
            return new InvalidRequestException("itemId", resourceId, "Item not found in shop");
        }
        return new GameNotFoundException(gameId, e);
    }

    private DragonsApiException handlePurchaseBadRequest(HttpClientErrorException e) {
        String body = getResponseBody(e);
        if (body != null && (body.contains("gold") || body.contains("insufficient"))) {
            return new InsufficientResourcesException("Not enough gold to purchase item");
        }
        return new InvalidRequestException("Purchase failed: " + body);
    }

    private String getResponseBody(Exception e) {
        if (e instanceof HttpClientErrorException httpError) {
            return httpError.getResponseBodyAsString();
        } else if (e instanceof HttpServerErrorException httpError) {
            return httpError.getResponseBodyAsString();
        }
        return e.getMessage();
    }

    private String buildUrl(String... segments) {
        return baseUrl + "/" + String.join("/", segments);
    }

    private String snippet(String body) {
        if (body == null) return "null";
        return body.length() <= 200 ? body : body.substring(0, 200) + "...";
    }
}
