package com.isa.solution.service;

import com.isa.solution.apiclient.DragonsApiClient;
import com.isa.solution.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class GamePlayService {

    private static final Logger log = LoggerFactory.getLogger(GamePlayService.class);
    private final DragonsApiClient apiClient;
    private final MissionSelectionService missionSelectionService;
    private final ShopService shopService;

    private static final int TARGET_SCORE = 1000;
    private static final int MAX_SKIPS_BEFORE_FORCE = 5;

    public GamePlayService(DragonsApiClient apiClient, MissionSelectionService missionSelectionService, ShopService shopService) {
        this.apiClient = apiClient;
        this.missionSelectionService = missionSelectionService;
        this.shopService = shopService;
    }

    public GamePlayResponse playGame() {
        log.info("Starting game...");
        GameResult result = playSingleGame();
        return buildGamePlayResponse(result);
    }

    private GameResult playSingleGame() {
        Game game;
        try {
            game = apiClient.startGame();
            String gameId = game.gameId();

            Set<String> attemptedInThisTurn = new HashSet<>();
            int consecutiveSkips = 0;

            while (game.lives() > 0) {
                if (game.score() >= TARGET_SCORE) {
                    log.info("[{}] Target reached. Score: {}", gameId, game.score());
                    break;
                }

                if (game.lives() < 3 && game.gold() >= 50) {
                    Game bought = tryBuyPotion(game);
                    if (bought.lives() > game.lives()) {
                        game = bought;
                        consecutiveSkips = 0;
                    }
                }

                List<Message> missions = apiClient.getAllMissions(gameId);
                missions.removeIf(m -> attemptedInThisTurn.contains(m.adId()));

                Optional<Message> selectedMission = missionSelectionService.selectBestMission(
                        missions, game.lives()
                );

                if (selectedMission.isEmpty()) {
                    consecutiveSkips++;
                    log.debug("[{}] No safe missions (Attempt {}/{})", gameId, consecutiveSkips, MAX_SKIPS_BEFORE_FORCE);

                    if (consecutiveSkips >= MAX_SKIPS_BEFORE_FORCE) {
                        log.warn("[{}] STUCK IN LOOP. Forcing fallback mission.", gameId);
                        selectedMission = missionSelectionService.selectFallbackMission(missions);
                    }
                }

                if (selectedMission.isEmpty()) {
                    attemptedInThisTurn.clear();
                    continue;
                }

                Message mission = selectedMission.get();
                attemptedInThisTurn.add(mission.adId());

                consecutiveSkips = 0;

                try {
                    log.info("[{}] Solving: {} ({}) - Reward: {}", gameId, mission.probability(), mission.adId(), mission.reward());
                    SolveResponse resp = apiClient.solveMission(gameId, mission.adId());

                    game = new Game(gameId, resp.lives(), resp.gold(), game.level(), resp.score(), resp.turn(), resp.highScore());
                    attemptedInThisTurn.clear();

                } catch (Exception e) {
                    log.warn("[{}] Solve failed: {}", gameId, e.getMessage());
                }
            }

            return new GameResult(gameId, game.score(), game.turn(), game.score() >= TARGET_SCORE, null);

        } catch (Exception e) {
            log.error("Game Error", e);
            return new GameResult("error", 0, 0, false, e.getMessage());
        }
    }

    private Game tryBuyPotion(Game game) {
        try {
            Optional<PurchaseResponse> p = shopService.buyHealingPotionIfNeeded(game.gameId(), game.gold(), game.lives());
            if (p.isPresent()) {
                return new Game(game.gameId(), p.get().lives(), p.get().gold(), p.get().level(), game.score(), p.get().turn(), game.highScore());
            }
        } catch(Exception ignored) {
        }
        return game;
    }

    private GamePlayResponse buildGamePlayResponse(GameResult result) {
        boolean success = result.success();
        int score = result.finalScore();
        return new GamePlayResponse(1, success ? 1 : 0, success ? 0 : 1, score, score, score, List.of(result));
    }
}
