package com.etsuni.games;

import com.etsuni.games.commands.Commands;
import com.etsuni.games.games.Events;
import com.etsuni.games.menus.MenuListener;
import com.etsuni.games.menus.PlayerMenuUtility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public final class Games extends JavaPlugin {

    private File mainMenuFile;
    private FileConfiguration mainMenuConfig;
    private File coinflipFile;
    private FileConfiguration coinflipConfig;
    private File rpsFile;
    private FileConfiguration rpsConfig;
    private File crashFile;
    private FileConfiguration crashConfig;

    private Commands commands;

    public Economy econ = null;
    private static final Logger log = Logger.getLogger("Minecraft");

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        commands = new Commands(this);
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        createConfigs();
        this.getCommand("games").setExecutor(commands);
        this.getCommand("cf").setExecutor(commands);
        this.getCommand("rps").setExecutor(commands);

        this.getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        this.getServer().getPluginManager().registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {

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

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEcon() {
        return this.econ;
    }
}
