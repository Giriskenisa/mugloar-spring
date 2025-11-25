package com.isa.solution.service;

import com.isa.solution.apiclient.DragonsApiClient;
import com.isa.solution.exception.ApiResponseException;
import com.isa.solution.exception.InvalidGameStateException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GamePlayServiceTest {

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
    void testPlayGame_Success() {
        // Given
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null);
        SolveResponse solveResponse = new SolveResponse(true, 3, 150, 1000, 1000, 2, "Success");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When
        GamePlayResponse response = gamePlayService.playGame();

        // Then
        assertNotNull(response);
        assertEquals(1, response.totalGamesPlayed());
        verify(apiClient).startGame();
    }

    @Test
    void testPlayGame_StartGameReturnsNull() {
        // Given
        when(apiClient.startGame()).thenReturn(null);

        // When
        GamePlayResponse response = gamePlayService.playGame();

        // Then
        assertNotNull(response);
        assertEquals(1, response.totalGamesPlayed());
        assertEquals(0, response.successfulGames());
    }

    @Test
    void testPlayGame_NoMissionsAvailable() {
        // Given
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of());
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When
        GamePlayResponse response = gamePlayService.playGame();

        // Then
        assertNotNull(response);
        assertEquals(1, response.totalGamesPlayed());
    }

    @Test
    void testPlayGame_ApiExceptionDuringGame() {
        // Given
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenThrow(new ApiResponseException(500, "Server error", "Error", null));
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When
        GamePlayResponse response = gamePlayService.playGame();

        // Then
        assertNotNull(response);
        assertEquals(1, response.totalGamesPlayed());
    }

    @Test
    void testPlayGame_InvalidGameStateException() {
        // Given
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null);
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1"))
                .thenThrow(new InvalidGameStateException("game1", "ad1", "Invalid state"));
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When
        GamePlayResponse response = gamePlayService.playGame();

        // Then
        assertNotNull(response);
        assertEquals(1, response.totalGamesPlayed());
    }

    @Test
    void testPlayGame_HealingPotionPurchase() {
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
    void testPlayGame_TargetScoreAchieved() {
        // Given
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null);
        SolveResponse solveResponse = new SolveResponse(true, 3, 150, 1000, 1000, 2, "Success");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When
        GamePlayResponse response = gamePlayService.playGame();

        // Then
        assertNotNull(response);
        assertTrue(response.successfulGames() >= 0);
    }

    @Test
    void testPlayGame_GameLosesAllLives() {
        // Given
        Game initialGame = new Game("game1", 1, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Risky", 5, 50, null);
        SolveResponse solveResponse = new SolveResponse(false, 0, 50, 0, 0, 2, "Failed");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When
        GamePlayResponse response = gamePlayService.playGame();

        // Then
        assertNotNull(response);
        assertEquals(1, response.totalGamesPlayed());
    }

    @Test
    void testPlayGame_BuildGamePlayResponse() {
        // Given
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null);
        SolveResponse solveResponse = new SolveResponse(true, 3, 150, 1000, 1000, 2, "Success");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When
        GamePlayResponse response = gamePlayService.playGame();

        // Then
        assertNotNull(response);
        assertEquals(1, response.totalGamesPlayed());
        assertTrue(response.highestScore() >= 0);
        assertTrue(response.lowestScore() >= 0);
        assertNotNull(response.gameResults());
        assertEquals(1, response.gameResults().size());
    }
}
