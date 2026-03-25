package dev.aurelium.slate.fabric.inv;

import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.inv.content.InventoryContents;
import dev.aurelium.slate.fabric.menu.SlateMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class SlateInventory {

    private final Component title;
    private final int rows;
    private final int columns;

    private final Slate slate;
    private final InventoryManager manager;
    private final MenuInventory menuInventory;

    public SlateInventory(Slate slate, InventoryManager manager, MenuInventory menuInventory, Component title, int rows) {
        this.slate = slate;
        this.manager = manager;
        this.menuInventory = menuInventory;
        this.title = title;
        this.rows = rows;
        this.columns = 9;
    }

    public Slate getSlate() {
        return slate;
    }

    public InventoryManager getManager() {
        return manager;
    }

    public Component getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public MenuInventory getMenuInventory() {
        return menuInventory;
    }

    public void open(ServerPlayer player) {
        SlateInventory oldInv = manager.getInventory(player);
        if (oldInv != null) {
            manager.setInventory(player, null);
        }

        InventoryContents contents = new InventoryContents(this, player.getUUID());
        manager.setContents(player, contents);

        try {
            menuInventory.init(player, contents);

            if (!this.manager.getContents(player).equals(Optional.of(contents))) {
                return;
            }

            SlateMenuProvider menuProvider = new SlateMenuProvider(this);
            player.openMenu(menuProvider);

            manager.setInventory(player, this);
            manager.cancelUpdateTask(player);
            manager.scheduleUpdateTask(player, this);
        } catch (Exception e) {
            e.printStackTrace();
            close(player);
        }
    }

    public void close(ServerPlayer player) {
        menuInventory.close();

        manager.setInventory(player, null);
        player.closeContainer();
        manager.setContents(player, null);
        manager.cancelUpdateTask(player);
    }

    public boolean checkBounds(int row, int col) {
        if (row < 0 || col < 0)
            return false;
        return row < this.rows && col < this.columns;
    }

}
