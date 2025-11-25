package com.isa.solution.service;

import com.isa.solution.apiclient.DragonsApiClient;
import com.isa.solution.exception.InsufficientResourcesException;
import com.isa.solution.model.PurchaseResponse;
import com.isa.solution.model.ShopItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    private static final Logger log = LoggerFactory.getLogger(ShopService.class);
    private final DragonsApiClient apiClient;

    public ShopService(DragonsApiClient apiClient) {
        this.apiClient = apiClient;
    }

    private static final String HEALING_POTION = "hpot";
    private static final int MIN_GOLD_RESERVE = 40;
    private static final int GOLD_THRESHOLD_HIGH_PRIORITY = 100;

    public Optional<PurchaseResponse> buyHealingPotionIfNeeded(String gameId, int currentGold, int currentLives) {
        if (currentLives >= 3) {
            log.debug("Lives are sufficient ({}), no need to buy healing potion", currentLives);
            return Optional.empty();
        }

        if (currentLives == 2 && currentGold < MIN_GOLD_RESERVE) {
            log.debug("Lives at 2 but gold too low ({}), skipping potion", currentGold);
            return Optional.empty();
        }

        if (currentGold >= GOLD_THRESHOLD_HIGH_PRIORITY || currentLives == 1) {
            log.info("High priority healing - Lives: {}, Gold: {}", currentLives, currentGold);
        } else if (currentGold < MIN_GOLD_RESERVE) {
            log.debug("Insufficient gold ({}) to buy healing potion safely", currentGold);
            return Optional.empty();
        }

        try {
            List<ShopItem> shopItems = apiClient.getShopItems(gameId);
            Optional<ShopItem> healingPotion = findHealingPotion(shopItems);

            if (healingPotion.isEmpty()) {
                log.debug("Healing potion not available in shop");
                return Optional.empty();
            }

            ShopItem potion = healingPotion.get();
            if (currentGold < potion.cost()) {
                log.debug("Cannot afford healing potion (cost: {}, gold: {})", potion.cost(), currentGold);
                return Optional.empty();
            }

            log.info("Purchasing healing potion for {} gold", potion.cost());
            PurchaseResponse response = apiClient.purchaseItem(gameId, potion.id());
            log.info("Purchase successful. Lives: {}, Gold remaining: {}", response.lives(), response.gold());
            return Optional.of(response);

        } catch (InsufficientResourcesException e) {
            log.warn("Failed to purchase healing potion: insufficient resources");
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error while trying to purchase healing potion", e);
            return Optional.empty();
        }
    }

    private Optional<ShopItem> findHealingPotion(List<ShopItem> shopItems) {
        return shopItems.stream()
                .filter(item -> HEALING_POTION.equalsIgnoreCase(item.id()))
                .findFirst();
    }
}
