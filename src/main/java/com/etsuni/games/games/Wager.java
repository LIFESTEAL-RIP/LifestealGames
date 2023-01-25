package com.etsuni.games.games;

import com.etsuni.games.Games;
import com.etsuni.games.menus.coinflip.CoinflipMainMenu;
import com.etsuni.games.menus.rps.RPSChoiceMenu;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Wager {

    public Long amount;
    public Player player;
    public GameType gameType;

    private final Games plugin;

    public Wager(Player player, GameType gameType, Long amount, Games plugin) {
        this.player = player;
        this.gameType = gameType;
        this.amount = amount;
        this.plugin = plugin;
    }

    public Boolean sendWager() {
        player.closeInventory();

        if(plugin.getEcon().getBalance(player) < amount) {
            return false;
        }

        plugin.getEcon().withdrawPlayer(player, amount);

        Configuration config = plugin.getWagersConfig();
        switch (gameType) {
            case COINFLIP:
                Coinflip coinflip = new Coinflip(player, amount, plugin);
                coinflip.addToGamesList();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        Objects.requireNonNull(config.getString("sent_wager")).replace("%amount%", getAmount().toString())
                                .replace("%game%", getGameType().toString())));
                break;
            case RPS:
                RPS rps = new RPS(player, amount, plugin);
                RPSChoiceMenu rpsChoiceMenu = new RPSChoiceMenu(plugin.getPlayerMenuUtility(player), rps, plugin);
                rpsChoiceMenu.open();
                break;
            case CRASH:
                Crash crash = new Crash(player, amount, plugin);
                player.closeInventory();
                crash.start();
                break;
            default:
                break;
        }


        return true;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }
}
