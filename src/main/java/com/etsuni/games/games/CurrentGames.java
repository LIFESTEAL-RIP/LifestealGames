package com.etsuni.games.games;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentGames {

    private List<Coinflip> coinflipGames = new ArrayList<>();
    //TODO ADD RPS AND CRASH LISTS
    private static CurrentGames instance = new CurrentGames();

    private CurrentGames() {

    }

    public static CurrentGames getInstance() {
        return instance;
    }

    public List<Coinflip> getCoinflipGames() {
        return coinflipGames;
    }
}
