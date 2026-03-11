package dev.aurelium.slate.fabric.menu;

import dev.aurelium.slate.fabric.inv.ClickableItem;
import dev.aurelium.slate.fabric.inv.SlateInventory;
import dev.aurelium.slate.fabric.inv.content.InventoryContents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SlateMenuProvider implements MenuProvider {

    private final SlateInventory slateInv;

    public SlateMenuProvider(SlateInventory slateInv) {
        this.slateInv = slateInv;
    }

    @Override
    public boolean shouldCloseCurrentScreen() {
        return false;
    }

    @Override
    public @NonNull Component getDisplayName() {
        return slateInv.getTitle();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NonNull Inventory inventory, @NonNull Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return null;

        MenuType<ChestMenu> menuType = getMenuType(slateInv.getRows());
        Container container = new SimpleContainer(slateInv.getRows() * slateInv.getColumns());

        // Fill the menu with items
        InventoryContents contents = slateInv.getManager().getContents(serverPlayer).orElseThrow();
        ClickableItem[][] items = contents.all();
        for (int row = 0; row < items.length; row++) {
            for (int column = 0; column < items[row].length; column++) {
                if (items[row][column] != null) {
                    container.setItem(9 * row + column, items[row][column].getItemIfVisible(serverPlayer));
                }
            }
        }

        return new ChestMenu(menuType, i, inventory, container, slateInv.getRows());
    }

    private MenuType<ChestMenu> getMenuType(int rows) {
        return switch (rows) {
            case 1 -> MenuType.GENERIC_9x1;
            case 2 -> MenuType.GENERIC_9x2;
            case 3 -> MenuType.GENERIC_9x3;
            case 4 -> MenuType.GENERIC_9x4;
            case 5 -> MenuType.GENERIC_9x5;
            default -> MenuType.GENERIC_9x6;
        };
    }
}
