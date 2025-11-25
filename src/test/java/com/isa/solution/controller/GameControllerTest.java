package com.isa.solution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isa.solution.apiclient.DragonsApiClient;
import com.isa.solution.model.*;
import com.isa.solution.service.GamePlayService;
import com.isa.solution.service.MissionSelectionService;
import com.isa.solution.service.ShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock
    private DragonsApiClient apiClient;

    private MissionSelectionService missionSelectionService;
    private ShopService shopService;
    private GamePlayService gamePlayService;
    private GameController gameController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        missionSelectionService = new MissionSelectionService();
        shopService = new ShopService(apiClient);
        gamePlayService = new GamePlayService(apiClient, missionSelectionService, shopService);
        gameController = new GameController(gamePlayService);
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testPlayGame_Success() throws Exception {
        // Given
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null);
        SolveResponse solveResponse = new SolveResponse(true, 3, 150, 1000, 1000, 2, "Success");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When & Then
        mockMvc.perform(post("/api/game/play")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalGamesPlayed").value(1));

        verify(apiClient, times(1)).startGame();
    }

    @Test
    void testPlayGame_ValidRequest() {
        // Given
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null);
        SolveResponse solveResponse = new SolveResponse(true, 3, 150, 1000, 1000, 2, "Success");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When
        ResponseEntity<GamePlayResponse> result = gameController.playGame();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().totalGamesPlayed());
    }

    @Test
    void testPlayGame_SingleGame() {
        // Given
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null);
        SolveResponse solveResponse = new SolveResponse(true, 3, 150, 1000, 1000, 2, "Success");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When
        ResponseEntity<GamePlayResponse> result = gameController.playGame();

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().totalGamesPlayed());
    }

    @Test
    void testPlayGame_NoRequestBody() throws Exception {
        // Given & When & Then
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null);
        SolveResponse solveResponse = new SolveResponse(true, 3, 150, 1000, 1000, 2, "Success");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        mockMvc.perform(post("/api/game/play")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
