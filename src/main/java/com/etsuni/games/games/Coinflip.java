package com.etsuni.games.games;

import com.etsuni.games.Games;
import com.etsuni.games.menus.coinflip.CoinflipGameMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class Coinflip extends TwoPlayerGame{

    public Player currentPlayerHead;
    public int taskId;
    private final Games plugin;
    private int flipCount = 0;

    public Coinflip(Player player1, Long wager, Games plugin) {
        super(player1, wager, plugin);
        this.plugin = plugin;
    }

    public void addToGamesList(){
        CurrentGames.getInstance().getCoinflipGames().add(this);
    }

    public void start() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        CoinflipGameMenu p1Menu = new CoinflipGameMenu(plugin.getPlayerMenuUtility(player1), plugin, this);
        CoinflipGameMenu p2Menu = new CoinflipGameMenu(plugin.getPlayerMenuUtility(player2), plugin, this);
        p1Menu.open();
        p2Menu.open();

        taskId = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(flipCount <= plugin.getCoinflipConfig().getInt("game_menu.flip_count")) {
                    if(currentPlayerHead.equals(player1)) {
                        currentPlayerHead = player2;

                    } else if(currentPlayerHead.equals(player2)){
                        currentPlayerHead = player1;
                    }
                    p1Menu.setPlayerHead(currentPlayerHead);
                    p2Menu.setPlayerHead(currentPlayerHead);
                } else {
                    scheduler.cancelTask(taskId);
                    return;
                }
                flipCount++;
            }
        },0, plugin.getCoinflipConfig().getInt("game_menu.flip_frequency"));
    }
}
