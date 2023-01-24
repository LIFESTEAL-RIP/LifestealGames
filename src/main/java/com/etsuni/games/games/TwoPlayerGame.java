package com.etsuni.games.games;

import com.etsuni.games.Games;
import org.bukkit.entity.Player;


public class TwoPlayerGame {

    public Player player1;
    public Player player2;
    public Long wager;

    private final Games plugin;

    public TwoPlayerGame(Player player1, Long wager, Games plugin) {
        this.player1 = player1;
        this.wager = wager;
        this.plugin = plugin;
    }

    public Double giveRewards(Integer tax, Player winner, Double amount) {
        double taxed = amount * (tax / 100.00);
        double finalAmount = (amount - taxed) * 2;
        plugin.getEcon().depositPlayer(winner, finalAmount);
        return finalAmount;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Long getWager() {
        return wager;
    }

    public void setWager(Long wager) {
        this.wager = wager;
    }





}
