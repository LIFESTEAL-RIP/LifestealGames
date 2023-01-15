package com.etsuni.games.games;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChatWagers {

    private Map<Player, GameType> wagersBeingWaitedOn = new HashMap<>();
    private static ChatWagers instance = new ChatWagers();

    private ChatWagers() {

    }

    public static ChatWagers getInstance() {
        return instance;
    }

    public Map<Player, GameType> getWaitingList() {
        return wagersBeingWaitedOn;
    }
}
