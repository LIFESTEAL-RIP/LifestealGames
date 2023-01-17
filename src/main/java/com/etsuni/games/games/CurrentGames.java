package com.etsuni.games.games;


import java.util.ArrayList;
import java.util.List;

public class CurrentGames {

    private List<Coinflip> coinflipGames = new ArrayList<>();
    private List<RPS> rpsGames = new ArrayList<>();
    private List<Crash> crashGames = new ArrayList<>();

    private static CurrentGames instance = new CurrentGames();

    private CurrentGames() {

    }

    public static CurrentGames getInstance() {
        return instance;
    }

    public List<Coinflip> getCoinflipGames() {
        return coinflipGames;
    }

    public List<RPS> getRpsGames() {
        return rpsGames;
    }

    public List<Crash> getCrashGames() {
        return crashGames;
    }
}
