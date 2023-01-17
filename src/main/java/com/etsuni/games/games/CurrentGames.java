package com.etsuni.games.games;


import java.util.ArrayList;
import java.util.List;

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
