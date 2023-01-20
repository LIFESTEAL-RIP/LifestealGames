package com.etsuni.games.games;

import com.etsuni.games.Games;
import com.etsuni.games.menus.crash.CrashGameMenu;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.text.DecimalFormat;
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
        },0, 5);
    }

    public void crash() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.cancelTask(taskId);
    }

    public void giveRewards() {

        int tax = plugin.getCrashConfig().getInt("settings.tax_amount");
        double amount = getWager() *  currentMultiplier;
        double taxed = amount * (tax / 100.00);
        double finalAmount = (amount - taxed);

        plugin.getEcon().depositPlayer(player, finalAmount);

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

    public void setWager(Long wager) {
        this.wager = wager;
    }

    public Double getCurrentMultiplier() {
        return currentMultiplier;
    }

    public void setCurrentMultiplier(Double currentMultiplier) {
        this.currentMultiplier = currentMultiplier;
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

    public void setCurrentIndexReducer(int currentIndexReducer) {
        this.currentIndexReducer = currentIndexReducer;
    }

    public List<Integer> getGlassSlots() {
        return glassSlots;
    }

    public boolean isCrashed() {
        return crashed;
    }
}
