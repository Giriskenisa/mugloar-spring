package com.isa.solution.controller;

import com.isa.solution.model.GamePlayResponse;
import com.isa.solution.service.GamePlayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    private final GamePlayService gamePlayService;

    public GameController(GamePlayService gamePlayService) {
        this.gamePlayService = gamePlayService;
    }

    @PostMapping("/play")
    public ResponseEntity<GamePlayResponse> playGame() {
        log.info("Received request to play game");

        GamePlayResponse response = gamePlayService.playGame();

        log.info("Completed playing game. Success: {}, Score: {}",
                response.successfulGames() > 0,
                response.highestScore());

        return ResponseEntity.ok(response);
    }
}
