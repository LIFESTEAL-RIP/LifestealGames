package com.etsuni.games.games;

import com.etsuni.games.Games;
import com.etsuni.games.menus.crash.CrashGameMenu;
import com.etsuni.games.utils.DBUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Crash {

    private final Games plugin;

    public Player player;
    public Long wager;

    public Double currentMultiplier;

    private int taskId;

    private final int HEAD_STARTING_INDEX = 36;
    private final int HEAD_END_INDEX = 8;
    private int currentHeadIndex = HEAD_STARTING_INDEX;
    private boolean stepUp = false;
    private int currentIndexReducer = 8;
    private List<Integer> glassSlots = new ArrayList<>();
    private boolean crashed = false;
    private boolean cashed_out = false;

    public Crash(Player player, Long wager, Games plugin) {
        this.plugin = plugin;
        this.player = player;
        this.wager = wager;
        this.currentMultiplier = 1.00D;
    }

    public void start() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        removeFromGamesList();
        CrashGameMenu menu = new CrashGameMenu(plugin.getPlayerMenuUtility(player), this, plugin);
        Configuration config = plugin.getCrashConfig();
        ConfigurationSection section = config.getConfigurationSection("game_menu");

        taskId = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                int chance = section.getInt("crash_chance");
                boolean val = rand.nextInt(100) < chance;
                if(val) {
                    crash();
                    crashed = true;
                    menu.open();
                } else {
                    menu.open();
                }
                currentMultiplier = currentMultiplier + 0.01D;
            }
        },0, 10);
    }

    public void crash() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.cancelTask(taskId);
        if(!cashed_out) {
            DBUtils dbUtils = new DBUtils(plugin);
            dbUtils.addLossToPlayer(player, "crash");
        }
    }

    public Double giveRewards() {
        cashed_out = true;
        int tax = plugin.getCrashConfig().getInt("settings.tax_amount");
        double amount = getWager() *  currentMultiplier;
        double taxed = amount * (tax / 100.00);
        Double finalAmount = (amount - taxed);

        DBUtils dbUtils = new DBUtils(plugin);
        dbUtils.addWinAndProfitToPlayer(player, "crash", finalAmount.longValue());

        plugin.getEcon().depositPlayer(player, finalAmount);
        return finalAmount;
    }

    public void addToGamesList() {
        CurrentGames.getInstance().getCrashGames().add(this);
    }

    public void removeFromGamesList() {
        CurrentGames.getInstance().getCrashGames().remove(this);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Long getWager() {
        return wager;
    }

    public Double getCurrentMultiplier() {
        return currentMultiplier;
    }

    public Double getCurrentWinnings() {
        return (wager * currentMultiplier);
    }

    public int getHEAD_STARTING_INDEX() {
        return HEAD_STARTING_INDEX;
    }

    public int getHEAD_END_INDEX() {
        return HEAD_END_INDEX;
    }

    public int getCurrentHeadIndex() {
        return currentHeadIndex;
    }

    public void setCurrentHeadIndex(int currentHeadIndex) {
        this.currentHeadIndex = currentHeadIndex;
    }

    public boolean isStepUp() {
        return stepUp;
    }

    public void setStepUp(boolean stepUp) {
        this.stepUp = stepUp;
    }

    public int getCurrentIndexReducer() {
        return currentIndexReducer;
    }

    public List<Integer> getGlassSlots() {
        return glassSlots;
    }

    public boolean isCrashed() {
        return crashed;
    }

    public boolean isCashed_out() {
        return cashed_out;
    }
}
