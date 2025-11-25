package com.isa.solution.apiclient;

import com.isa.solution.exception.ApiResponseException;
import com.isa.solution.exception.InvalidRequestException;
import com.isa.solution.model.Game;
import com.isa.solution.model.Message;
import com.isa.solution.model.PurchaseResponse;
import com.isa.solution.model.ShopItem;
import com.isa.solution.model.SolveResponse;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@TestPropertySource(properties = "dragons.api.base-url=https://dragonsofmugloar.com/api/v2")
class DragonsApiClientImplTest {

    private static final String BASE_URL = "https://dragonsofmugloar.com/api/v2";

    @Autowired
    private DragonsApiClientImpl apiClient;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testStartGameSuccess() throws IOException {
        String responseJson = "{\"gameId\":\"game1\",\"lives\":3,\"gold\":100,\"level\":1,\"score\":0,\"turn\":1,\"highScore\":0}";
        mockServer.expect(requestTo(BASE_URL + "/game/start"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        Game result = apiClient.startGame();

        assertNotNull(result);
        assertEquals("game1", result.gameId());
        mockServer.verify();
    }

    @Test
    void testStartGameHttpClientError() {
        mockServer.expect(requestTo(BASE_URL + "/game/start"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThrows(ApiResponseException.class, () -> apiClient.startGame());
        mockServer.verify();
    }

    @Test
    void testGetAllMissionsSuccess() throws IOException {
        String gameId = "game1";
        String responseJson = "[{\"adId\":\"ad1\",\"message\":\"Mission 1\",\"probability\":\"Sure Thing\"}]";
        mockServer.expect(requestTo(BASE_URL + "/" + gameId + "/messages"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        List<Message> result = apiClient.getAllMissions(gameId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ad1", result.get(0).adId());
        mockServer.verify();
    }

    @Test
    void testSolveMissionSuccess() throws IOException {
        String gameId = "game1";
        String missionId = "ad1";
        String responseJson = "{\"success\":true,\"lives\":3,\"gold\":150,\"score\":100}";
        mockServer.expect(requestTo(BASE_URL + "/" + gameId + "/solve/" + missionId))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        SolveResponse result = apiClient.solveMission(gameId, missionId);

        assertTrue(result.success());
        mockServer.verify();
    }

    @Test
    void testGetShopItemsSuccess() throws IOException {
        String gameId = "game1";
        String responseJson = "[{\"id\":\"hpot\",\"name\":\"Healing Potion\",\"cost\":50}]";
        mockServer.expect(requestTo(BASE_URL + "/" + gameId + "/shop"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        List<ShopItem> result = apiClient.getShopItems(gameId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("hpot", result.get(0).id());
        mockServer.verify();
    }

    @Test
    void testPurchaseItemSuccess() throws IOException {
        String gameId = "game1";
        String itemId = "hpot";
        String responseJson = "{\"shoppingSuccess\":true,\"lives\":3,\"gold\":50}";
        mockServer.expect(requestTo(BASE_URL + "/" + gameId + "/shop/buy/" + itemId))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        PurchaseResponse result = apiClient.purchaseItem(gameId, itemId);

        assertTrue(result.shoppingSuccess());
        mockServer.verify();
    }

    @Test
    void testInvalidId() {
        assertThrows(InvalidRequestException.class, () -> apiClient.getAllMissions(null));
        assertThrows(InvalidRequestException.class, () -> apiClient.solveMission("game1", ""));
        assertThrows(InvalidRequestException.class, () -> apiClient.getShopItems("  "));
        assertThrows(InvalidRequestException.class, () -> apiClient.purchaseItem(null, "item1"));
    }
}
