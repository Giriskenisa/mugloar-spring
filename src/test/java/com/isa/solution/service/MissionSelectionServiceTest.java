package com.isa.solution.service;

import com.isa.solution.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MissionSelectionServiceTest {

    private MissionSelectionService missionSelectionService;

    @BeforeEach
    void setUp() {
        missionSelectionService = new MissionSelectionService();
    }

    @Test
    void testSelectBestMission_EmptyList() {
        Optional<Message> result = missionSelectionService.selectBestMission(new ArrayList<>(), 3);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSelectBestMission_NullList() {
        Optional<Message> result = missionSelectionService.selectBestMission(null, 3);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSelectBestMission_ExpiredMission() {
        List<Message> missions = List.of(
                new Message("ad1", "Mission 1", "Sure Thing", 0, 100, null)
        );
        Optional<Message> result = missionSelectionService.selectBestMission(missions, 3);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSelectBestMission_SelectsHighestScore() {
        List<Message> missions = List.of(
                new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null),
                new Message("ad2", "Mission 2", "Sure Thing", 5, 100, null),
                new Message("ad3", "Mission 3", "Sure Thing", 5, 75, null)
        );
        Optional<Message> result = missionSelectionService.selectBestMission(missions, 3);
        assertTrue(result.isPresent());
        assertEquals("ad2", result.get().adId());
        assertEquals(100, result.get().reward());
    }

    @Test
    void testSelectBestMission_ConsidersProbability() {
        List<Message> missions = List.of(
                new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null),
                new Message("ad2", "Mission 2", "Risky", 5, 100, null)
        );
        Optional<Message> result = missionSelectionService.selectBestMission(missions, 3);
        assertTrue(result.isPresent());
        assertEquals("ad1", result.get().adId());
    }

    @Test
    void testSelectBestMission_ConsidersUrgency() {
        List<Message> missions = List.of(
                new Message("ad1", "Mission 1", "Sure Thing", 1, 50, null),
                new Message("ad2", "Mission 2", "Sure Thing", 5, 50, null)
        );
        Optional<Message> result = missionSelectionService.selectBestMission(missions, 3);
        assertTrue(result.isPresent());
        assertEquals("ad1", result.get().adId());
    }

    @Test
    void testSelectBestMission_FiltersByLives_OneLife() {
        List<Message> missions = List.of(
                new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null),
                new Message("ad2", "Mission 2", "Risky", 5, 100, null),
                new Message("ad3", "Mission 3", "Impossible", 5, 200, null)
        );
        Optional<Message> result = missionSelectionService.selectBestMission(missions, 1);
        assertTrue(result.isPresent());
        assertEquals("ad1", result.get().adId());
    }

    @Test
    void testSelectBestMission_FiltersByLives_TwoLives() {
        List<Message> missions = List.of(
                new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null),
                new Message("ad2", "Mission 2", "Risky", 5, 100, null),
                new Message("ad3", "Mission 3", "Impossible", 5, 200, null)
        );
        Optional<Message> result = missionSelectionService.selectBestMission(missions, 2);
        assertTrue(result.isPresent());
        assertNotEquals("ad3", result.get().adId());
    }

    @Test
    void testSelectBestMission_FiltersByLives_ThreeOrMoreLives() {
        List<Message> missions = List.of(
                new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null),
                new Message("ad2", "Mission 2", "Risky", 5, 100, null),
                new Message("ad3", "Mission 3", "Impossible", 5, 200, null)
        );
        Optional<Message> result = missionSelectionService.selectBestMission(missions, 3);
        assertTrue(result.isPresent());
        assertNotEquals("ad3", result.get().adId());
    }

    @Test
    void testSelectBestMission_NullProbability() {
        List<Message> missions = List.of(
                new Message("ad1", "Mission 1", null, 5, 50, null)
        );
        Optional<Message> result = missionSelectionService.selectBestMission(missions, 3);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSelectBestMission_AllProbabilityLevels() {
        List<Message> missions = List.of(
                new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null),
                new Message("ad2", "Mission 2", "Piece of Cake", 5, 50, null),
                new Message("ad3", "Mission 3", "Walk in the park", 5, 50, null),
                new Message("ad4", "Mission 4", "Quite likely", 5, 50, null),
                new Message("ad5", "Mission 5", "Hmmm....", 5, 50, null),
                new Message("ad6", "Mission 6", "Risky", 5, 50, null),
                new Message("ad7", "Mission 7", "Playing with fire", 5, 50, null),
                new Message("ad8", "Mission 8", "Suicide mission", 5, 50, null)
        );
        Optional<Message> result = missionSelectionService.selectBestMission(missions, 3);
        assertTrue(result.isPresent());
        assertEquals("ad1", result.get().adId());
    }

    @Test
    void testSelectBestMission_HighRewardWithLifeBonus() {
        List<Message> missions = List.of(
                new Message("ad1", "Mission 1", "Sure Thing", 5, 50, null),
                new Message("ad2", "Mission 2", "Risky", 5, 100, null)
        );
        Optional<Message> result = missionSelectionService.selectBestMission(missions, 3);
        assertTrue(result.isPresent());
        assertEquals("ad1", result.get().adId());
    }
}

