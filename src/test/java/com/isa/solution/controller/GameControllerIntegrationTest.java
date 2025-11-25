package com.isa.solution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isa.solution.apiclient.DragonsApiClient;
import com.isa.solution.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DragonsApiClient apiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPlayGame_Integration_Success() throws Exception {
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalGamesPlayed").value(1));
    }

    @Test
    void testPlayGame_Integration_AllGamesFailed() throws Exception {
        // Given
        Game initialGame = new Game("game1", 1, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Risky", 5, 50, null);
        SolveResponse solveResponse = new SolveResponse(false, 0, 50, 0, 0, 2, "Failed");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When & Then
        mockMvc.perform(post("/api/game/play")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalGamesPlayed").value(1))
                .andExpect(jsonPath("$.failedGames").value(1));
    }

    @Test
    void testPlayGame_Integration_AllGamesSuccessful() throws Exception {
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
                .andExpect(jsonPath("$.totalGamesPlayed").value(1))
                .andExpect(jsonPath("$.successfulGames").exists());
    }

    @Test
    void testPlayGame_Integration_NoContentType() throws Exception {
        // Given
        Game initialGame = new Game("game1", 3, 100, 1, 0, 1, 0);
        Message mission = new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null);
        SolveResponse solveResponse = new SolveResponse(true, 3, 150, 1000, 1000, 2, "Success");
        
        when(apiClient.startGame()).thenReturn(initialGame);
        when(apiClient.getAllMissions("game1")).thenReturn(List.of(mission));
        lenient().when(apiClient.solveMission("game1", "ad1")).thenReturn(solveResponse);
        lenient().when(apiClient.getShopItems(anyString())).thenReturn(List.of());

        // When & Then
        mockMvc.perform(post("/api/game/play"))
                .andExpect(status().isOk());
    }
}
