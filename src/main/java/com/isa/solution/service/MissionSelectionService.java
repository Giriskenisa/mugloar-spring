package com.isa.solution.service;

import com.isa.solution.model.Message;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MissionSelectionService {

    private static final String P_SURE = "Sure Thing";
    private static final String P_CAKE = "Piece of Cake";
    private static final String P_PARK = "Walk in the park";
    private static final String P_LIKELY = "Quite likely";
    private static final String P_HMMM = "Hmmm....";
    private static final String P_RISKY = "Risky";
    private static final String P_FIRE = "Playing with fire";
    private static final String P_SUICIDE = "Suicide mission";
    private static final String P_IMPOSSIBLE = "Impossible";

    public Optional<Message> selectBestMission(List<Message> missions, int currentLives) {
        if (missions == null || missions.isEmpty()) return Optional.empty();

        return missions.stream()
                .filter(m -> m.expiresIn() > 0)
                .filter(m -> isSafeEnough(m.probability(), currentLives))
                .max(Comparator.comparingDouble(this::calculateExpectedValue));
    }

    public Optional<Message> selectFallbackMission(List<Message> missions) {
        if (missions == null || missions.isEmpty()) return Optional.empty();

        return missions.stream()
                .filter(m -> m.expiresIn() > 0)
                .filter(m -> !P_IMPOSSIBLE.equals(m.probability()) && !P_SUICIDE.equals(m.probability()))
                .max(Comparator.comparingDouble(m -> getNumericProbability(m.probability())));
    }

    private boolean isSafeEnough(String probability, int lives) {
        if (probability == null) return false;

        if (lives <= 1) {
            return P_SURE.equals(probability) || P_CAKE.equals(probability);
        }

        if (lives <= 2) {
            return P_SURE.equals(probability) ||
                    P_CAKE.equals(probability) ||
                    P_PARK.equals(probability);
        }

        return switch (probability) {
            case P_SURE, P_CAKE, P_PARK, P_LIKELY -> true;
            default -> false;
        };
    }

    private double calculateExpectedValue(Message mission) {
        return mission.reward() * getNumericProbability(mission.probability());
    }

    private double getNumericProbability(String p) {
        if (p == null) return 0.0;
        return switch (p) {
            case P_SURE -> 1.0;
            case P_CAKE -> 0.95;
            case P_PARK -> 0.85;
            case P_LIKELY -> 0.70;
            case P_HMMM -> 0.50;
            case P_RISKY -> 0.35;
            case P_FIRE -> 0.20;
            case P_SUICIDE -> 0.05;
            default -> 0.0;
        };
    }
}
