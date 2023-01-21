package com.etsuni.games.menus.crash;

import com.etsuni.games.Games;
import com.etsuni.games.games.Crash;
import com.etsuni.games.menus.Menu;
import com.etsuni.games.menus.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CrashGameMenu extends Menu {

    private final Games plugin;
    public Crash crash;

    public CrashGameMenu(PlayerMenuUtility playerMenuUtility, Crash crash, Games plugin) {
        super(playerMenuUtility, plugin);
        this.plugin = plugin;
        this.crash = crash;
    }

    @Override
    public String getMenuName() {
        return ChatColor.translateAlternateColorCodes('&',plugin.getCrashConfig().getString("main_menu.title"));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if(crash.isCrashed()) {
            return;
        }

        if(slot == 49 ) {

            if(crash.isCashed_out()) {
                return;
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getCrashConfig().getString("settings.messages.win")
                            .replace("%win_amount%", String.valueOf(crash.giveRewards().longValue()))));

        }
    }

    @Override
    public void setMenuItems() {
        int ENDING_POINT = 54;
        int MIDDLE = 49;
        for(int i = 45; i < ENDING_POINT; i++ ) {
            if(i == MIDDLE) {
                continue;
            }
            else {
                ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(" ");
                itemStack.setItemMeta(meta);
                inventory.setItem(i, itemStack);
            }
        }
        if(crash.isCrashed()) {
            doCrashAnimation();
        }
        setPlayerHead();
        setCashOutItem();
        setGlassPanes();
    }

    public void setPlayerHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(playerMenuUtility.getOwner());
        head.setItemMeta(headMeta);

        ItemStack glass = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        int glassSlot = crash.getCurrentHeadIndex();

        if(animate()) {
            if(crash.isStepUp()) {
                crash.setCurrentHeadIndex(crash.getCurrentHeadIndex() - crash.getCurrentIndexReducer());
                inventory.setItem(crash.getCurrentHeadIndex(), head);
                crash.getGlassSlots().add(glassSlot);
                crash.setStepUp(false);
            }
            else {
                if(crash.getCurrentHeadIndex() == crash.getHEAD_END_INDEX()) {
                    crash.setCurrentHeadIndex(crash.getHEAD_STARTING_INDEX());
                    crash.getGlassSlots().clear();
                    inventory.setItem(crash.getCurrentHeadIndex(), head);
                    return;
                }
                crash.setCurrentHeadIndex(crash.getCurrentHeadIndex() + 1);
                inventory.setItem(crash.getCurrentHeadIndex(), head);
                crash.getGlassSlots().add(glassSlot);
                crash.setStepUp(true);
            }
        } else {
            inventory.setItem(crash.getCurrentHeadIndex(), head);
        }


    }

    public void setGlassPanes() {
        ItemStack glass = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        for(Integer i : crash.getGlassSlots()) {
            inventory.setItem(i, glass);
        }

    }

    public boolean animate() {
        if(crash.getCurrentMultiplier() == 1.00) {
            return false;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        String multToString = df.format(crash.getCurrentMultiplier());
        String[] multStringArr = multToString.split("\\.");
        int decimalPlaceNumber = Integer.parseInt(multStringArr[1]);


        return decimalPlaceNumber % 10 == 0;
    }

    public void doCrashAnimation() {
        int STOP_INDEX = 45;
        ItemStack redGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = redGlass.getItemMeta();
        meta.setDisplayName(" ");
        redGlass.setItemMeta(meta);

        if(crash.getCurrentHeadIndex() > 35 && crash.getCurrentHeadIndex() < 45) {
            inventory.setItem(crash.getCurrentHeadIndex(), redGlass);
        }

        for(int i = crash.getCurrentHeadIndex() + 9; i < STOP_INDEX; i += 9) {
            inventory.setItem(i, redGlass);
        }
    }

    public void setCashOutItem() {
        int MIDDLE = 49;
        Configuration config = plugin.getCrashConfig();
        ConfigurationSection menu = config.getConfigurationSection("game_menu");
        ItemStack itemStack = new ItemStack(Material.valueOf(menu.getString("cash_out_item.material")));
        ItemMeta meta = itemStack.getItemMeta();
        if(crash.isCrashed()) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', menu.getString("cash_out_item.name_crashed")));
        } else {
            if(crash.isCashed_out()) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', menu.getString("cash_out_item.name_cashed_out")));
            } else {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', menu.getString("cash_out_item.name_not_crashed")));
            }
        }

        List<String> lore = new ArrayList<>();
        for(String s : menu.getStringList("cash_out_item.lore")) {
            if(s.contains("%multiplier%")) {
                DecimalFormat df = new DecimalFormat("0.00");
                lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("%multiplier%", df.format(crash.getCurrentMultiplier()))));
            }
            else if(s.contains("%current_winnings%")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("%current_winnings%", String.valueOf(crash.getCurrentWinnings().longValue()))));
            }

        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        inventory.setItem(MIDDLE, itemStack);
    }
}
