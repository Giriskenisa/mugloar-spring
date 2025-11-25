package com.isa.solution.service;

import com.isa.solution.apiclient.DragonsApiClient;
import com.isa.solution.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GamePlayServiceIntegrationTest {

    @Mock
    private DragonsApiClient apiClient;

    private MissionSelectionService missionSelectionService;
    private ShopService shopService;
    private GamePlayService gamePlayService;

    @BeforeEach
    void setUp() {
        missionSelectionService = new MissionSelectionService();
        shopService = new ShopService(apiClient);
        gamePlayService = new GamePlayService(apiClient, missionSelectionService, shopService);
    }

    @Test
    void testPlayGame_Integration_CompleteGameFlow() {
        // Given
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        Message mission1 = new Message("ad1", "Mission 1", "Sure Thing", 5, 100, null);
        Message mission2 = new Message("ad2", "Mission 2", "Piece of Cake", 5, 150, null);
        SolveResponse solveResponse1 = new SolveResponse(true, 3, 200, 500, 500, 2, "Success");
        SolveResponse solveResponse2 = new SolveResponse(true, 3, 350, 1000, 1000, 3, "Success");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());
        when(apiClient.getAllMissions("game1"))
                .thenReturn(List.of(mission1, mission2))
                .thenReturn(List.of(mission1));
        lenient().when(apiClient.solveMission("game1", "ad2")).thenReturn(solveResponse1);
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse2);

        // When
        GamePlayResponse response = gamePlayService.playGame();

        // Then
        assertNotNull(response);
        assertEquals(1, response.totalGamesPlayed());
        assertTrue(response.averageScore() >= 0);
        verify(apiClient, atLeastOnce()).startGame();
        verify(apiClient, atLeastOnce()).getAllMissions("game1");
    }

    @Test
    void testPlayGame_Integration_WithHealingPotionPurchase() {
        // Given
        Game initialGame = new Game("game1", 2, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null);
        PurchaseResponse purchaseResponse = new PurchaseResponse(true, 3, 50, 1, 1);
        SolveResponse solveResponse = new SolveResponse(true, 3, 100, 1000, 1000, 2, "Success");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getShopItems("game1")).thenReturn(List.of(new ShopItem("hpot", "Healing Potion", 50)));
        when(apiClient.purchaseItem("game1", "hpot")).thenReturn(purchaseResponse);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse);

        // When
        GamePlayResponse response = gamePlayService.playGame();

        // Then
        assertNotNull(response);
        verify(apiClient, atLeastOnce()).getShopItems("game1");
        verify(apiClient).purchaseItem("game1", "hpot");
    }

    @Test
    void testPlayGame_Integration_SingleGame() {
        // Given
        Game game1 = new Game("game1", 3, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null);
        SolveResponse solveResponse = new SolveResponse(true, 3, 150, 1000, 1000, 2, "Success");
        
        when(apiClient.startGame()).thenReturn(game1);
        when(apiClient.getAllMissions(anyString())).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission(anyString(), anyString())).thenReturn(solveResponse);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When
        GamePlayResponse response = gamePlayService.playGame();

        // Then
        assertNotNull(response);
        assertEquals(1, response.totalGamesPlayed());
        verify(apiClient, times(1)).startGame();
    }
}
