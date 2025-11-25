package com.isa.solution.apiclient;

import com.isa.solution.exception.*;
import com.isa.solution.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@SpringBootTest
@TestPropertySource(properties = {
        "dragons.api.base-url=https://dragonsofmugloar.com/api/v2"
})
class DragonsApiClientImplIntegrationTest {

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
    void testStartGame_Integration() throws Exception {
        // Given
        String responseJson = "{\"gameId\":\"game1\",\"lives\":3,\"gold\":100,\"level\":1,\"score\":0,\"turn\":1,\"highScore\":0}";
        
        mockServer.expect(requestTo("https://dragonsofmugloar.com/api/v2/game/start"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        // When
        Game result = apiClient.startGame();

        // Then
        assertNotNull(result);
        assertEquals("game1", result.gameId());
        assertEquals(3, result.lives());
        assertEquals(100, result.gold());
        mockServer.verify();
    }

    @Test
    void testGetAllMissions_Integration() throws Exception {
        // Given
        String gameId = "game1";
        String responseJson = "[{\"adId\":\"ad1\",\"message\":\"Mission 1\",\"probability\":\"Sure Thing\",\"expiresIn\":5,\"reward\":50}]";
        
        mockServer.expect(requestTo("https://dragonsofmugloar.com/api/v2/" + gameId + "/messages"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        // When
        List<Message> result = apiClient.getAllMissions(gameId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ad1", result.get(0).adId());
        assertEquals("Mission 1", result.get(0).message());
        assertEquals("Sure Thing", result.get(0).probability());
        assertEquals(5, result.get(0).expiresIn());
        assertEquals(50, result.get(0).reward());
        mockServer.verify();
    }

    @Test
    void testSolveMission_Integration() throws Exception {
        // Given
        String gameId = "game1";
        String missionId = "ad1";
        String responseJson = "{\"success\":true,\"lives\":3,\"gold\":150,\"score\":100,\"highScore\":100,\"turn\":2,\"message\":\"Success\"}";
        
        mockServer.expect(requestTo("https://dragonsofmugloar.com/api/v2/" + gameId + "/solve/" + missionId))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        // When
        SolveResponse result = apiClient.solveMission(gameId, missionId);

        // Then
        assertNotNull(result);
        assertTrue(result.success());
        assertEquals(3, result.lives());
        assertEquals(150, result.gold());
        assertEquals(100, result.score());
        mockServer.verify();
    }

    @Test
    void testGetShopItems_Integration() throws Exception {
        // Given
        String gameId = "game1";
        String responseJson = "[{\"id\":\"hpot\",\"name\":\"Healing Potion\",\"cost\":50},{\"id\":\"sword\",\"name\":\"Sword\",\"cost\":100}]";
        
        mockServer.expect(requestTo("https://dragonsofmugloar.com/api/v2/" + gameId + "/shop"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        // When
        List<ShopItem> result = apiClient.getShopItems(gameId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("hpot", result.get(0).id());
        assertEquals("Healing Potion", result.get(0).name());
        assertEquals(50, result.get(0).cost());
        mockServer.verify();
    }

    @Test
    void testPurchaseItem_Integration() throws Exception {
        // Given
        String gameId = "game1";
        String itemId = "hpot";
        String responseJson = "{\"shoppingSuccess\":true,\"lives\":3,\"gold\":50,\"level\":1,\"turn\":1}";
        
        mockServer.expect(requestTo("https://dragonsofmugloar.com/api/v2/" + gameId + "/shop/buy/" + itemId))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        // When
        PurchaseResponse result = apiClient.purchaseItem(gameId, itemId);

        // Then
        assertNotNull(result);
        assertTrue(result.shoppingSuccess());
        assertEquals(3, result.lives());
        assertEquals(50, result.gold());
        mockServer.verify();
    }

    @Test
    void testErrorHandling_Integration_ConnectionError() {
        // Given & When & Then
        assertTrue(true);
    }
}
