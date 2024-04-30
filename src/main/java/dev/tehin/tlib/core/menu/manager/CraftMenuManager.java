package dev.tehin.tlib.core.menu.manager;

import dev.tehin.tlib.core.menu.Menu;
import dev.tehin.tlib.api.menu.MenuType;
import dev.tehin.tlib.api.menu.manager.MenuManager;
import dev.tehin.tlib.api.tLib;
import dev.tehin.tlib.core.menu.listener.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Optional;

public class CraftMenuManager implements MenuManager {

    private final HashMap<MenuType, Menu> guis;

    public CraftMenuManager() {
        guis = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new MenuListener(this), tLib.get().getOwner());
    }

    /**
     * Safely get the Menu owner of specified inventory
     * @param inventory The inventory we are getting the Menu from
     * @return Empty {@link Optional} if not found, or wrapping {@link Menu} if found
     */
    public Optional<Menu> getMenu(Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof Menu)) return Optional.empty();

        return Optional.of((Menu) holder);
    }

    @Override
    public Menu getMenu(MenuType type) {
        return guis.get(type);
    }

    @Override
    public void register(Menu menu) {
        guis.put(menu.getType(), menu);
    }

    public void open(Player player, MenuType type) {
        guis.get(type).open(player);
    }


}
