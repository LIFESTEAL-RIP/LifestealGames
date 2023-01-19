package com.etsuni.games.games;

import com.etsuni.games.Games;
import com.etsuni.games.menus.coinflip.CoinflipGameMenu;
import com.etsuni.games.menus.crash.CrashGameMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class Crash {

    private final Games plugin;

    public Player player;
    public Long wager;

    private int taskId;

    public Crash(Player player, Long wager, Games plugin) {
        this.plugin = plugin;
        this.player = player;
        this.wager = wager;
    }

    public void start() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        removeFromGamesList();
        CrashGameMenu menu = new CrashGameMenu(plugin.getPlayerMenuUtility(player), this, plugin);
        menu.open();

        taskId = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {

            }
        },0, 10);
    }

    public void addToGamesList() {
        CurrentGames.getInstance().getCrashGames().add(this);
    }

    public void removeFromGamesList() {
        CurrentGames.getInstance().getCrashGames().remove(this);
    }
}
