package com.etsuni.games.menus.rps;

import com.etsuni.games.Games;
import com.etsuni.games.games.RPS;
import com.etsuni.games.menus.Menu;
import com.etsuni.games.menus.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;

public class RPSGameMenu extends Menu {

    private final Games plugin;
    private final RPS rps;

    public RPSGameMenu(PlayerMenuUtility playerMenuUtility, Games plugin, RPS rps) {
        super(playerMenuUtility, plugin);
        this.plugin = plugin;
        this.rps = rps;
    }

    @Override
    public String getMenuName() {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getRpsConfig().getString("game_menu.title")));
    }

    @Override
    public int getSlots() {
        return plugin.getRpsConfig().getInt("game_menu.size");
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {

    }

    @Override
    public void setMenuItems() {
        for(int i = 0; i < getInventory().getSize(); i++) {
            if(i == 4 || i == 13 || i == 22) {
                getInventory().setItem(i, new ItemStack(Material.valueOf(plugin.getRpsConfig().getString("game_menu.middle_glass_pane"))));
            }
            else {
                getInventory().setItem(i, new ItemStack(Material.valueOf(plugin.getRpsConfig().getString("game_menu.surrounding_glass_pane"))));
            }
        }
        setPlayerHeads(this.rps.getPlayer1(), this.rps.getPlayer2());
        setChoice(this.rps.getCounter());
    }

    public void setPlayerHeads(Player player1, Player player2) {
        ItemStack p1Head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta p1meta = (SkullMeta) p1Head.getItemMeta();
        ItemStack p2Head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta p2meta = (SkullMeta) p2Head.getItemMeta();
        p1meta.setOwningPlayer(player1);
        p2meta.setOwningPlayer(player2);
        p1Head.setItemMeta(p1meta);
        p2Head.setItemMeta(p2meta);
        inventory.setItem(10, p1Head);
        inventory.setItem(16, p2Head);
    }

    public void setChoice(Integer choiceId) {
        ItemStack itemStack = null;
        ItemMeta meta;

        switch (choiceId) {
            case 1:
                itemStack = new ItemStack(Material.STONE);
                meta = itemStack.getItemMeta();
                meta.setDisplayName("");
                itemStack.setItemMeta(meta);
                break;
            case 2:
                itemStack = new ItemStack(Material.PAPER);
                meta = itemStack.getItemMeta();
                meta.setDisplayName("");
                itemStack.setItemMeta(meta);
                break;
            case 3:
                itemStack = new ItemStack(Material.SHEARS);
                meta = itemStack.getItemMeta();
                meta.setDisplayName("");
                itemStack.setItemMeta(meta);
                break;
            default:
                break;
        }
        inventory.setItem(11, itemStack);
        inventory.setItem(15, itemStack);
    }

    public void setPlayersChoices(int p1, int p2) {
        ItemStack p1Item = null;
        ItemMeta p1ItemMeta;
        ItemStack p2Item = null;
        ItemMeta p2ItemMeta;

        switch (p1) {
            case 1:
                p1Item = new ItemStack(Material.STONE);
                p1ItemMeta = p1Item.getItemMeta();
                p1ItemMeta.setDisplayName("");
                p1Item.setItemMeta(p1ItemMeta);
                break;
            case 2:
                p1Item = new ItemStack(Material.PAPER);
                p1ItemMeta = p1Item.getItemMeta();
                p1ItemMeta.setDisplayName("");
                p1Item.setItemMeta(p1ItemMeta);
                break;
            case 3:
                p1Item = new ItemStack(Material.SHEARS);
                p1ItemMeta = p1Item.getItemMeta();
                p1ItemMeta.setDisplayName("");
                p1Item.setItemMeta(p1ItemMeta);
                break;
            default:
                break;
        }

        switch (p2) {
            case 1:
                p2Item = new ItemStack(Material.STONE);
                p2ItemMeta = p2Item.getItemMeta();
                p2ItemMeta.setDisplayName("");
                p2Item.setItemMeta(p2ItemMeta);
                break;
            case 2:
                p2Item = new ItemStack(Material.PAPER);
                p2ItemMeta = p2Item.getItemMeta();
                p2ItemMeta.setDisplayName("");
                p2Item.setItemMeta(p2ItemMeta);
                break;
            case 3:
                p2Item = new ItemStack(Material.SHEARS);
                p2ItemMeta = p2Item.getItemMeta();
                p2ItemMeta.setDisplayName("");
                p2Item.setItemMeta(p2ItemMeta);
                break;
            default:
                break;
        }

        inventory.setItem(11, p1Item);
        inventory.setItem(15, p2Item);
    }
}
