package com.etsuni.games.games;

import com.etsuni.games.Games;
import com.etsuni.games.menus.rps.RPSGameMenu;
import com.etsuni.games.utils.DBUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitScheduler;

public class RPS extends TwoPlayerGame {
    private final Games plugin;

    private Choice player1Choice;
    private Choice player2Choice;
    private int counter = 0;
    private int taskId;

    public enum Choice{
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

    public Boolean start(Choice p2Choice) {

        if(!CurrentGames.getInstance().getRpsGames().contains(this)) {
            return false;
        }

        player2Choice = p2Choice;

        removeFromList();
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        RPSGameMenu p1Menu = new RPSGameMenu(plugin.getPlayerMenuUtility(player1), plugin, this);
        RPSGameMenu p2Menu = new RPSGameMenu(plugin.getPlayerMenuUtility(player2), plugin, this);
        p1Menu.open();
        p2Menu.open();

//        plugin.getEcon().withdrawPlayer(player1, wager);
        plugin.getEcon().withdrawPlayer(player2, wager);

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
                            handleWinner();
                            player1.closeInventory();
                            player2.closeInventory();
                        }
                    }, 40);
                } else {
                    scheduler.cancelTask(taskId);
                    return;
                }
                counter++;
            }
        }, 10, 20);

        return true;
    }

    private void handleWinner() {
        Double winAmount;
        DBUtils dbUtils = new DBUtils(plugin);

        if(player1Choice.equals(Choice.ROCK) && player2Choice.equals(Choice.SCISSORS)) {
            winAmount = giveRewards(plugin.getRpsConfig().getInt("settings.tax_amount"), player1, wager.doubleValue());
            sendMessages(player1, winAmount);
            dbUtils.addWinAndProfitToPlayer(player1, "rps", winAmount.longValue());
            dbUtils.addLossToPlayer(player2, "rps");
            return;
        }
        else if(player1Choice.equals(Choice.ROCK) && player2Choice.equals(Choice.PAPER)) {
            winAmount = giveRewards(plugin.getRpsConfig().getInt("settings.tax_amount"), player2, wager.doubleValue());
            sendMessages(player2, winAmount);
            dbUtils.addWinAndProfitToPlayer(player2, "rps", winAmount.longValue());
            dbUtils.addLossToPlayer(player1, "rps");
            return;
        }
        else if(player1Choice.equals(Choice.PAPER) && player2Choice.equals(Choice.ROCK)) {
            winAmount = giveRewards(plugin.getRpsConfig().getInt("settings.tax_amount"), player1, wager.doubleValue());
            sendMessages(player1, winAmount);
            dbUtils.addWinAndProfitToPlayer(player1, "rps", winAmount.longValue());
            dbUtils.addLossToPlayer(player2, "rps");
            return;
        }
        else if(player1Choice.equals(Choice.PAPER) && player2Choice.equals(Choice.SCISSORS)) {
            winAmount = giveRewards(plugin.getRpsConfig().getInt("settings.tax_amount"), player2, wager.doubleValue());
            sendMessages(player2, winAmount);
            dbUtils.addWinAndProfitToPlayer(player2, "rps", winAmount.longValue());
            dbUtils.addLossToPlayer(player1, "rps");
            return;
        }
        else if(player1Choice.equals(Choice.SCISSORS) && player2Choice.equals(Choice.PAPER)) {
            winAmount = giveRewards(plugin.getRpsConfig().getInt("settings.tax_amount"), player1, wager.doubleValue());
            sendMessages(player1, winAmount);
            dbUtils.addWinAndProfitToPlayer(player1, "rps", winAmount.longValue());
            dbUtils.addLossToPlayer(player2, "rps");
            return;
        }
        else if(player1Choice.equals(Choice.SCISSORS) && player2Choice.equals(Choice.ROCK)) {
            winAmount = giveRewards(plugin.getRpsConfig().getInt("settings.tax_amount"), player2, wager.doubleValue());
            sendMessages(player2, winAmount);
            dbUtils.addWinAndProfitToPlayer(player2, "rps", winAmount.longValue());
            dbUtils.addLossToPlayer(player1, "rps");
            return;
        }

        //Give players money back if no one wins, with tax taken out. Might be up for change.
        winAmount = Double.valueOf(wager);

        plugin.getEcon().depositPlayer(player1, wager);
        plugin.getEcon().depositPlayer(player2, wager);

        sendMessages(null, winAmount);

    }

    private void sendMessages(Player winner, Double winAmount) {

        if(winner != null) {
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getRpsConfig().getString("settings.messages.win")
                            .replace("%winner%", ChatColor.stripColor(winner.getDisplayName()))
                            .replace("%win_amount%", String.valueOf(winAmount.longValue()))));

            player2.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getRpsConfig().getString("settings.messages.win")
                            .replace("%winner%", ChatColor.stripColor(winner.getDisplayName()))
                            .replace("%win_amount%", String.valueOf(winAmount.longValue()))));
        } else {
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getRpsConfig().getString("settings.messages.no_winner")
                            .replace("%amount%", String.valueOf(winAmount.longValue()))));

            player2.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getRpsConfig().getString("settings.messages.no_winner")
                            .replace("%amount%", String.valueOf(winAmount.longValue()))));
        }

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

    public int getCounter() {
        return counter;
    }
}
