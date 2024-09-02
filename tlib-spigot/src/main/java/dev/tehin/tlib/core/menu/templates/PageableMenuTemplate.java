package dev.tehin.tlib.core.menu.templates;

import dev.tehin.tlib.api.menu.action.MenuAction;
import dev.tehin.tlib.core.item.ItemBuilder;
import dev.tehin.tlib.core.menu.Menu;
import dev.tehin.tlib.core.menu.MenuContentBuilder;
import dev.tehin.tlib.core.menu.MenuTemplate;
import dev.tehin.tlib.core.menu.action.ExecutorAction;
import lombok.RequiredArgsConstructor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@RequiredArgsConstructor
public class PageableMenuTemplate implements MenuTemplate {

    private static final ItemBuilder PANE = new ItemBuilder(Material.STAINED_GLASS_PANE).name("&7").data(0);

    private final Menu menu;
    private final int currentPage;
    private final int maxPage;

    @Override
    public List<ItemStack> apply(List<ItemStack> items) {
        // Use builder to register actions
        MenuContentBuilder builder = new MenuContentBuilder(menu);

        final int maxContent = getMaxColumns() * getMaxRows();
        final boolean firstPage = currentPage == 0;

        int start = Math.max(0, (maxContent * currentPage) - 1);

        // If first page, get the max content minus one since we start from 0 and
        // not from our desired page start.
        int end = (firstPage) ? maxContent - 1 : ((start + (9 * getMaxRows()) - 1));

        // We add one since end is exclusive
        items = items.subList(start, Math.min(items.size(), end + 1));

        boolean isFull = items.size() >= maxContent;

        // Fill items if not full, try to adjust to the size of items if it's the first and only page
        // If not, fill the whole inventory so the menu does not change suddenly of size
        if (!isFull) fill(items, !firstPage);

        addSeparator(items);
        addOptions(items, builder);

        return items;
    }

    private void fill(List<ItemStack> items, boolean full) {
        if (!full) {
            while (items.size() % 9 != 0) {
                items.add(null);
            }
        } else {
            while (items.size() < getMaxRows() * getMaxColumns()) {
                items.add(null);
            }
        }
    }

    private void addEmpty(List<ItemStack> items, int quantity) {
        for (int i = 0; i < quantity; i++) {
            items.add(null);
        }
    }

    private ItemStack previous(MenuContentBuilder builder) {
        if (currentPage == 0) return new ItemStack(Material.AIR);

        MenuAction action = new ExecutorAction(player -> {
            menu.open(player, currentPage - 1);
        });

        // Parse since page starts from 0 and not from 1
        final int previousPageParsed = currentPage;

        ItemBuilder item = new ItemBuilder(Material.BANNER)
                .baseColor(DyeColor.WHITE)
                .addPattern(new Pattern(DyeColor.RED, PatternType.RHOMBUS_MIDDLE))
                .addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT))
                .addPattern(new Pattern(DyeColor.WHITE, PatternType.SQUARE_TOP_RIGHT))
                .addPattern(new Pattern(DyeColor.WHITE, PatternType.SQUARE_BOTTOM_RIGHT))
                .name("&a&lAnterior &7(Página #" + previousPageParsed + ")")
                .action(action)
                .amount(previousPageParsed);

        return builder.register(item);
    }

    private ItemStack next(MenuContentBuilder builder) {
        if (currentPage == maxPage) return new ItemStack(Material.AIR);

        MenuAction action = new ExecutorAction(player -> {
            menu.open(player, currentPage + 1);
        });

        // Parse since page starts from 0 and not from 1
        final int nextPageParsed = currentPage + 2;
        ItemBuilder item = new ItemBuilder(Material.BANNER)
                .baseColor(DyeColor.WHITE)
                .addPattern(new Pattern(DyeColor.GREEN, PatternType.RHOMBUS_MIDDLE))
                .addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT))
                .addPattern(new Pattern(DyeColor.WHITE, PatternType.SQUARE_TOP_LEFT))
                .addPattern(new Pattern(DyeColor.WHITE, PatternType.SQUARE_BOTTOM_LEFT))
                .name("&a&lSiguiente &7(Página #" + nextPageParsed + ")")
                .action(action)
                .amount(nextPageParsed);

        return builder.register(item);
    }

    private ItemBuilder filter() {
        return new ItemBuilder(Material.HOPPER)
                .name("&f&lFiltrar");
    }

    private void addSeparator(List<ItemStack> items) {
        ItemStack stack = PANE.build();

        for (int i = 0; i < 9; i++) {
            items.add(stack);
        }
    }

    private void addOptions(List<ItemStack> items, MenuContentBuilder builder) {
        items.add(previous(builder));
        addEmpty(items, 3);
        items.add(filter().build());
        addEmpty(items, 3);
        items.add(next(builder));
    }

    @Override
    public int getMaxRows() {
        return 3;
    }

    @Override
    public int getMaxColumns() {
        return 9;
    }
}