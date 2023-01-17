package com.etsuni.games.menus;

import com.etsuni.games.Games;
import com.etsuni.games.menus.Menu;
import com.etsuni.games.menus.PlayerMenuUtility;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected int maxItemsPerPage;
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility, Games plugin) {
        super(playerMenuUtility, plugin);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

    public void setMaxItemsPerPage(int maxItemsPerPage) {
        this.maxItemsPerPage = maxItemsPerPage;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
