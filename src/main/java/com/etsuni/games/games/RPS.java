package com.etsuni.games.games;

import com.etsuni.games.Games;
import com.etsuni.games.menus.rps.RPSGameMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class RPS extends TwoPlayerGame {
    private final Games plugin;

    private Choice player1Choice;
    private Choice player2Choice;
    private int counter = 1;
    private int taskId;

    enum Choice{
        ROCK(1),
        PAPER(2),
        SCISSORS(3);
        public final int val;
        Choice(int i) {
            this.val = i;
        }
    }

    public RPS(Player player1, Long wager, Games plugin) {
        super(player1, wager, plugin);
        this.plugin = plugin;

    }

    public void start() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        RPSGameMenu p1Menu = new RPSGameMenu(plugin.getPlayerMenuUtility(player1), plugin, this);
        RPSGameMenu p2Menu = new RPSGameMenu(plugin.getPlayerMenuUtility(player2), plugin, this);
        p1Menu.open();
        p2Menu.open();

        taskId = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(counter <= 3) {
                    p1Menu.setChoice(counter);
                    p2Menu.setChoice(counter);
                    p1Menu.open();
                    p2Menu.open();
                }else if(counter == 4) {
                    p1Menu.setPlayersChoices(player1Choice.val, player2Choice.val);
                    p2Menu.setPlayersChoices(player1Choice.val, player2Choice.val);
                    scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.broadcastMessage("winner!!");
                            player1.closeInventory();
                            player2.closeInventory();
                        }
                    }, 40);
                } else {
                    scheduler.cancelTask(taskId);
                    return;
                }
            }
        }, 10, 20);
    }

    private Player getWinner() {

        if(player1Choice.equals(Choice.ROCK) && player2Choice.equals(Choice.SCISSORS)) {
            return this.player1;
        }
        else if(player1Choice.equals(Choice.ROCK) && player2Choice.equals(Choice.PAPER)) {
            return this.player2;
        }
        else if(player1Choice.equals(Choice.PAPER) && player2Choice.equals(Choice.ROCK)) {
            return this.player1;
        }
        else if(player1Choice.equals(Choice.PAPER) && player2Choice.equals(Choice.SCISSORS)) {
            return this.player2;
        }

        return null;
    }

    public void addToList() {
        CurrentGames.getInstance().getRpsGames().add(this);
    }
    public void removeFromList() {
        CurrentGames.getInstance().getRpsGames().remove(this);
    }

    public void sendChoiceTitle(Player player) {
        Configuration config = plugin.getRpsConfig();
        player.sendTitle(
                ChatColor.translateAlternateColorCodes('&', config.getString("settings.messages.choice_pick.title")),
                ChatColor.translateAlternateColorCodes('&', config.getString("settings.messages.choice_pick.message")),
                config.getInt("settings.messages.choice_pick.fade_in"),
                config.getInt("settings.messages.choice_pick.stay"),
                config.getInt("settings.messages.choice_pick.fade_out")
                );
    }

    public Choice getPlayer1Choice() {
        return player1Choice;
    }

    public Choice getPlayer2Choice() {
        return player2Choice;
    }

    public void setPlayer1Choice(Choice p1Choice) {
        this.player1Choice = p1Choice;
    }

    public void setPlayer2Choice(Choice p2Choice) {
        this.player2Choice = p2Choice;
    }
}
