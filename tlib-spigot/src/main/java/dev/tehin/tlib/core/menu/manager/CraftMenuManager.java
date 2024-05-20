package dev.tehin.tlib.core.menu.manager;

import dev.tehin.tlib.api.menu.annotations.MenuProperties;
import dev.tehin.tlib.core.exceptions.MenuNotRegisteredException;
import dev.tehin.tlib.core.exceptions.NoPropertiesFoundException;
import dev.tehin.tlib.core.menu.Menu;
import dev.tehin.tlib.api.menu.manager.MenuManager;
import dev.tehin.tlib.api.tLib;
import dev.tehin.tlib.core.menu.listener.MenuListener;
import dev.tehin.tlib.utilities.PermissionUtil;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Optional;

public class CraftMenuManager implements MenuManager {

    private final HashMap<Class<? extends Menu>, Menu> menus;

    public CraftMenuManager() {
        menus = new HashMap<>();

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

    @SneakyThrows
    @Override
    public Menu getMenu(Class<? extends Menu> type) {
        Menu menu = menus.get(type);
        if (menu == null) throw new MenuNotRegisteredException(type);

        return menu;
    }

    @SneakyThrows
    @Override
    public void register(Menu... menus) {
        for (Menu menu : menus) {
            Class<? extends Menu> clazz = menu.getClass();
            MenuProperties properties = clazz.getAnnotation(MenuProperties.class);

            if (properties == null) throw new NoPropertiesFoundException(Menu.class);

            menu.setDisplay(properties.display());
            menu.setPermission(properties.permission());

            this.menus.put(clazz, menu);
        }
    }

    public void open(Player player, Class<? extends Menu> type) {
        Menu menu = menus.get(type);

        if (!PermissionUtil.has(player, menu.getPermission())) {
            PermissionUtil.sendMessage(player);
            return;
        }

        menu.open(player);
    }


}