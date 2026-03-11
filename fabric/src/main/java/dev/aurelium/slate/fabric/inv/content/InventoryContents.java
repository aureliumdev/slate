package dev.aurelium.slate.fabric.inv.content;

import dev.aurelium.slate.fabric.inv.ClickableItem;
import dev.aurelium.slate.fabric.inv.SlateInventory;
import dev.aurelium.slate.inv.content.SlotPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class InventoryContents {

    private final SlateInventory inv;
    private final UUID playerUuid;

    private final ClickableItem[][] contents;

    public InventoryContents(SlateInventory inv, UUID playerUuid) {
        this.inv = inv;
        this.playerUuid = playerUuid;
        this.contents = new ClickableItem[inv.getRows()][inv.getColumns()];
    }

    public SlateInventory inventory() {
        return inv;
    }

    public ClickableItem[][] all() {
        return contents;
    }

    public Optional<ClickableItem> get(int row, int column) {
        if (row < 0 || row >= contents.length)
            return Optional.empty();
        if (column < 0 || column >= contents[row].length)
            return Optional.empty();

        return Optional.ofNullable(contents[row][column]);
    }

    public Optional<ClickableItem> get(SlotPos slotPos) {
        return get(slotPos.getRow(), slotPos.getColumn());
    }

    public InventoryContents set(SlotPos pos, ClickableItem item) {
        return set(pos.getRow(), pos.getColumn(), item);
    }

    public InventoryContents set(int row, int column, ClickableItem item) {
        if (row < 0 || row >= contents.length)
            return this;
        if (column < 0 || column >= contents[row].length)
            return this;

        contents[row][column] = item;
        if (playerUuid != null) {
            ServerPlayer player = inv.getSlate().getServer().getPlayerList().getPlayer(playerUuid);
            if (player != null) {
                update(row, column, item == null ? null : item.getItemIfVisible(player), player);
            }
        }
        return this;
    }

    public InventoryContents fill(ClickableItem item) {
        for (int row = 0; row < contents.length; row++) {
            for (int column = 0; column < contents[row].length; column++) {
                set(row, column, item);
            }
        }
        return this;
    }

    private void update(int row, int column, ItemStack item, ServerPlayer player) {
        if (!inv.getManager().getOpenedPlayers(inv).contains(playerUuid)) {
            return;
        }

        AbstractContainerMenu containerMenu = player.containerMenu;
        if (containerMenu != player.inventoryMenu) { // Player has the slate menu open
            int slotNum = inv.getColumns() * row + column;
            containerMenu.setItem(slotNum, containerMenu.getStateId() + 1, item);
        }
    }
}
