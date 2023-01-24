package com.etsuni.games;

import com.etsuni.games.commands.Commands;
import com.etsuni.games.menus.MenuListener;
import com.etsuni.games.menus.PlayerMenuUtility;
import com.etsuni.games.utils.JoinListener;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Games extends JavaPlugin {

    private File mainMenuFile;
    private FileConfiguration mainMenuConfig;
    private File coinflipFile;
    private FileConfiguration coinflipConfig;
    private File rpsFile;
    private FileConfiguration rpsConfig;
    private File crashFile;
    private FileConfiguration crashConfig;
    private File mainConfigFile;
    private FileConfiguration mainConfig;
    private File wagersFile;
    private FileConfiguration wagersConfig;

    private Commands commands;

    public Economy econ = null;
    private static final Logger log = Logger.getLogger("Minecraft");

    private DataSource dataSource;

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
        this.getCommand("crash").setExecutor(commands);

        this.getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        try {
            dataSource = initMySQLDataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            initDb();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
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

        mainConfigFile = new File(getDataFolder(), "config.yml");
        if(!mainConfigFile.exists()) {
            mainConfigFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        mainConfig = new YamlConfiguration();

        try {
            mainConfig.load(mainConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        wagersFile = new File(getDataFolder(), "wagers.yml");
        if(!wagersFile.exists()) {
            wagersFile.getParentFile().mkdirs();
            saveResource("wagers.yml", false);
        }

        wagersConfig = new YamlConfiguration();

        try {
           wagersConfig.load(wagersFile);
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

    public FileConfiguration getWagersConfig() {
        return wagersConfig;
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

    private DataSource initMySQLDataSource() throws SQLException {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setServerName(mainConfig.getString("database.host"));
        dataSource.setPortNumber(mainConfig.getInt("database.port"));
        dataSource.setDatabaseName(mainConfig.getString("database.database"));
        dataSource.setUser(mainConfig.getString("database.user"));
        dataSource.setPassword(mainConfig.getString("database.password"));

        testDataSource(dataSource);
        return dataSource;
    }

    private void testDataSource(DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(1)) {
                throw new SQLException("Could not establish database connection.");
            }
        }
    }

    private void initDb() throws SQLException, IOException {
        String setup;
        try(InputStream in = getClassLoader().getResourceAsStream("dbsetup.sql")) {
            setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not read db setup file");
            throw e;
        }
        String[] queries = setup.split(";");
        // execute each query to the database.
        for (String query : queries) {
            // If you use the legacy way you have to check for empty queries here.
            if (query.isEmpty()) continue;
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.execute();
            }
        }
        getLogger().info("Database setup complete");
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }
}
