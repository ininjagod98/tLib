package dev.tehin.tlib.core.menu.listener;

import dev.tehin.tlib.core.menu.Menu;
import dev.tehin.tlib.api.menu.action.MenuAction;
import dev.tehin.tlib.core.menu.manager.CraftMenuManager;
import dev.tehin.tlib.utilities.item.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class MenuListener implements Listener {

    private final CraftMenuManager manager;

    public MenuListener(CraftMenuManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Optional<Menu> type = manager.getMenu(e.getInventory());
        if (!type.isPresent()) return;

        e.setCancelled(true);

        Menu menu = type.get();
        Optional<String> id = ItemUtils.getTag(e.getCurrentItem(), "action");

        if (!id.isPresent()) return;

        MenuAction action = menu.getAction(Integer.parseInt(id.get()));
        if (action.getType() != e.getClick()) return;

        action.execute((Player) e.getWhoClicked());
    }
}
