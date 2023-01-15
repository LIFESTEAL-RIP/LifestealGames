package com.etsuni.games;

import com.etsuni.games.menus.PlayerMenuUtility;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class Games extends JavaPlugin {

    private File mainMenuFile;
    private FileConfiguration mainMenuConfig;
    private File coinflipFile;
    private FileConfiguration coinflipConfig;
    private File rpsFile;
    private FileConfiguration rpsConfig;
    private File crashFile;
    private FileConfiguration crashConfig;

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        createConfigs();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void createConfigs() {
        mainMenuFile = new File(getDataFolder(), "mainmenu.yml");
        if(!mainMenuFile.exists()) {
            mainMenuFile.getParentFile().mkdirs();
            saveResource("mainmenu.yml", false);
        }

        mainMenuConfig = new YamlConfiguration();

        try {
            mainMenuConfig.load(mainMenuFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        coinflipFile = new File(getDataFolder(), "coinflip.yml");
        if(!coinflipFile.exists()) {
            coinflipFile.getParentFile().mkdirs();
            saveResource("coinflip.yml", false);
        }

        coinflipConfig = new YamlConfiguration();

        try {
            coinflipConfig.load(coinflipFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        rpsFile = new File(getDataFolder(), "rps.yml");
        if(!rpsFile.exists()) {
            rpsFile.getParentFile().mkdirs();
            saveResource("rps.yml", false);
        }

        rpsConfig = new YamlConfiguration();

        try {
            rpsConfig.load(rpsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        crashFile = new File(getDataFolder(), "crash.yml");
        if(!crashFile.exists()) {
            crashFile.getParentFile().mkdirs();
            saveResource("crash.yml", false);
        }

        crashConfig = new YamlConfiguration();

        try {
            crashConfig.load(crashFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getMainMenuConfig() {
        return mainMenuConfig;
    }

    public FileConfiguration getCoinflipConfig() {
        return coinflipConfig;
    }

    public FileConfiguration getRpsConfig() {
        return rpsConfig;
    }

    public FileConfiguration getCrashConfig() {
        return crashConfig;
    }

    public PlayerMenuUtility getPlayerMenuUtility(Player player) {
        PlayerMenuUtility playerMenuUtility;

        if(playerMenuUtilityMap.containsKey(player)) {
            return playerMenuUtilityMap.get(player);
        }
        else {
            playerMenuUtility = new PlayerMenuUtility(player);
            playerMenuUtilityMap.put(player, playerMenuUtility);

            return playerMenuUtility;
        }
    }
}
