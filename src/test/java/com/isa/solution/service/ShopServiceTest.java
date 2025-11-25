package com.isa.solution.service;

import com.isa.solution.apiclient.DragonsApiClient;
import com.isa.solution.exception.InsufficientResourcesException;
import com.isa.solution.model.PurchaseResponse;
import com.isa.solution.model.ShopItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private DragonsApiClient apiClient;

    private ShopService shopService;

    @BeforeEach
    void setUp() {
        shopService = new ShopService(apiClient);
    }

    @Test
    void testBuyHealingPotionIfNeeded_SufficientLives() {
        Optional<PurchaseResponse> result = shopService.buyHealingPotionIfNeeded("game1", 100, 3);
        assertTrue(result.isEmpty());
        verify(apiClient, never()).getShopItems(anyString());
    }

    @Test
    void testBuyHealingPotionIfNeeded_LivesAt2InsufficientGold() {
        Optional<PurchaseResponse> result = shopService.buyHealingPotionIfNeeded("game1", 30, 2);
        assertTrue(result.isEmpty());
        verify(apiClient, never()).getShopItems(anyString());
    }

    @Test
    void testBuyHealingPotionIfNeeded_HealingPotionNotAvailable() {
        when(apiClient.getShopItems("game1")).thenReturn(List.of(
                new ShopItem("sword", "Sword", 50),
                new ShopItem("shield", "Shield", 30)
        ));

        Optional<PurchaseResponse> result = shopService.buyHealingPotionIfNeeded("game1", 100, 2);
        assertTrue(result.isEmpty());
        verify(apiClient, never()).purchaseItem(anyString(), anyString());
    }

    @Test
    void testBuyHealingPotionIfNeeded_CannotAfford() {
        when(apiClient.getShopItems("game1")).thenReturn(List.of(
                new ShopItem("hpot", "Healing Potion", 100)
        ));

        Optional<PurchaseResponse> result = shopService.buyHealingPotionIfNeeded("game1", 50, 2);
        assertTrue(result.isEmpty());
        verify(apiClient, never()).purchaseItem(anyString(), anyString());
    }

    @Test
    void testBuyHealingPotionIfNeeded_SuccessfulPurchase() {
        when(apiClient.getShopItems("game1")).thenReturn(List.of(
                new ShopItem("hpot", "Healing Potion", 50)
        ));
        PurchaseResponse purchaseResponse = new PurchaseResponse(true, 3, 50, 1, 1);
        when(apiClient.purchaseItem("game1", "hpot")).thenReturn(purchaseResponse);

        Optional<PurchaseResponse> result = shopService.buyHealingPotionIfNeeded("game1", 100, 2);
        assertTrue(result.isPresent());
        assertEquals(purchaseResponse, result.get());
        verify(apiClient).purchaseItem("game1", "hpot");
    }

    @Test
    void testBuyHealingPotionIfNeeded_OneLifeHighPriority() {
        when(apiClient.getShopItems("game1")).thenReturn(List.of(
                new ShopItem("hpot", "Healing Potion", 50)
        ));
        PurchaseResponse purchaseResponse = new PurchaseResponse(true, 2, 50, 1, 1);
        when(apiClient.purchaseItem("game1", "hpot")).thenReturn(purchaseResponse);

        Optional<PurchaseResponse> result = shopService.buyHealingPotionIfNeeded("game1", 100, 1);
        assertTrue(result.isPresent());
        verify(apiClient).purchaseItem("game1", "hpot");
    }

    @Test
    void testBuyHealingPotionIfNeeded_HighGoldThreshold() {
        when(apiClient.getShopItems("game1")).thenReturn(List.of(
                new ShopItem("hpot", "Healing Potion", 50)
        ));
        PurchaseResponse purchaseResponse = new PurchaseResponse(true, 3, 50, 1, 1);
        when(apiClient.purchaseItem("game1", "hpot")).thenReturn(purchaseResponse);

        Optional<PurchaseResponse> result = shopService.buyHealingPotionIfNeeded("game1", 100, 2);
        assertTrue(result.isPresent());
        verify(apiClient).purchaseItem("game1", "hpot");
    }

    @Test
    void testBuyHealingPotionIfNeeded_InsufficientResourcesException() {
        when(apiClient.getShopItems("game1")).thenReturn(List.of(
                new ShopItem("hpot", "Healing Potion", 50)
        ));
        when(apiClient.purchaseItem("game1", "hpot")).thenThrow(new InsufficientResourcesException("gold"));

        Optional<PurchaseResponse> result = shopService.buyHealingPotionIfNeeded("game1", 100, 2);
        assertTrue(result.isEmpty());
    }

    @Test
    void testBuyHealingPotionIfNeeded_GenericException() {
        when(apiClient.getShopItems("game1")).thenReturn(List.of(
                new ShopItem("hpot", "Healing Potion", 50)
        ));
        when(apiClient.purchaseItem("game1", "hpot")).thenThrow(new RuntimeException("Network error"));

        Optional<PurchaseResponse> result = shopService.buyHealingPotionIfNeeded("game1", 100, 2);
        assertTrue(result.isEmpty());
    }

    @Test
    void testBuyHealingPotionIfNeeded_ExceptionGettingShopItems() {
        when(apiClient.getShopItems("game1")).thenThrow(new RuntimeException("API error"));

        Optional<PurchaseResponse> result = shopService.buyHealingPotionIfNeeded("game1", 100, 2);
        assertTrue(result.isEmpty());
        verify(apiClient, never()).purchaseItem(anyString(), anyString());
    }

    @Test
    void testBuyHealingPotionIfNeeded_CaseInsensitiveHealingPotion() {
        when(apiClient.getShopItems("game1")).thenReturn(List.of(
                new ShopItem("HPOT", "Healing Potion", 50)
        ));
        PurchaseResponse purchaseResponse = new PurchaseResponse(true, 3, 50, 1, 1);
        when(apiClient.purchaseItem("game1", "HPOT")).thenReturn(purchaseResponse);

        Optional<PurchaseResponse> result = shopService.buyHealingPotionIfNeeded("game1", 100, 2);
        assertTrue(result.isPresent());
        verify(apiClient).purchaseItem("game1", "HPOT");
    }
}

