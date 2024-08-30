package dev.tehin.tlib.core.menu;

import dev.tehin.tlib.api.menu.action.MenuAction;
import dev.tehin.tlib.api.menu.action.data.ItemData;
import dev.tehin.tlib.api.menu.features.StaticMenu;
import dev.tehin.tlib.core.item.ItemBuilder;
import dev.tehin.tlib.core.menu.options.MenuOptions;
import dev.tehin.tlib.utilities.MessageUtil;
import dev.tehin.tlib.utilities.PermissionUtil;
import dev.tehin.tlib.utilities.task.TaskUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public abstract class Menu implements InventoryHolder {

    @Getter
    private final MenuActions actions = new MenuActions();

    private @Setter String display;
    private @Setter @Getter String permission;
    private @Setter @Getter String noPermissionMessage = PermissionUtil.getDefaultMessage();

    private @Getter final MenuOptions options = new MenuOptions();
    private Inventory inventory;

    protected abstract MenuContentBuilder create(Player player);

    protected MenuContentBuilder createContentBuilder() {
        return new MenuContentBuilder(this);
    }

    public void open(Player player) {
        TaskUtil.runSyncLater(() -> player.playSound(player.getLocation(), getOptions().soundOnOpen(), 0.5f, 1f), 2);

        player.openInventory(get(player));
    }

    protected Inventory get(Player player) {
        if (this instanceof StaticMenu && inventory != null) return getInventory();

        List<ItemStack> items = create(player).build();

        while (items.size() % 9 != 0) {
            items.add(null);
        }

        Inventory inventory = Bukkit.createInventory(this, items.size(), MessageUtil.color(display));
        inventory.setContents(items.toArray(new ItemStack[0]));

        // If the inventory has not already been created, assign it
        if (this instanceof StaticMenu) this.inventory = inventory;

        return inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void reload() {
        if (!(this instanceof StaticMenu)) {
            throw new UnsupportedOperationException("The menu is not static, please implement StaticMenu");
        }

        this.actions.clear();
        this.inventory = null;
    }

    /**
     * Updates the item in the position, changing their data and
     * lore if material is the same, if not, the ItemStack will be replaced
     * @param position Position of the item
     * @param builder Item to update
     */
    public boolean update(int position, ItemBuilder builder) {
        if (!(this instanceof StaticMenu)) {
            throw new UnsupportedOperationException("The menu is not static, please implement StaticMenu");
        }

        // Prevent updates if no one has opened the inventory yet
        if (getInventory() == null) {
            System.out.println("Item could not be updated due to inventory not being opened yet");
            return false;
        }

        ItemStack found = getInventory().getItem(position);
        if (found == null) {
            System.out.println("Item could not be updated due to position being off");
            return false;
        }

        ItemData data = new ItemData(found.getItemMeta().getDisplayName(), found.getItemMeta().getLore());

        // Get id based on our item properties
        Optional<MenuAction> action = getActions().get(data);
        if (action.isEmpty()) {
            System.out.println("Item could not be updated due to action not being found");
            return false;
        }

        // Set the item properties
        builder.apply(found);
        action.get().setData(new ItemData(found.getItemMeta().getDisplayName(), found.getItemMeta().getLore()));

        return true;
    }

    /**
     * Tries to find the position based on the given item, then executes
     * {@link Menu#update(int, ItemBuilder)} with the {@link ItemBuilder} and its position
     * @param builder ItemStack that will replace or update the existent one
     * @return If the {@link ItemStack} was found and replaced
     */
    public boolean update(ItemBuilder builder) {
        throw new UnsupportedOperationException("Not implemented yet, please use Menu#update(int, ItemBuilder)");
    }

}
