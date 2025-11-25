package com.isa.solution.apiclient;

import com.isa.solution.model.*;

import java.util.List;


public interface DragonsApiClient {

    Game startGame();

    List<Message> getAllMissions(String gameId);

    SolveResponse solveMission(String gameId, String missionId);

    List<ShopItem> getShopItems(String gameId);

    PurchaseResponse purchaseItem(String gameId, String itemId);
}