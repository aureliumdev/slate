package dev.aurelium.slate.fabric.item;

import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.info.MenuInfo;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.menu.ActiveMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Stores contextual data for when a player clicks an item.
 */
public class ItemClick extends MenuInfo {

    private final ItemStack item;
    private final SlotPos pos;

    public ItemClick(Slate slate, ServerPlayer player, ItemStack item, SlotPos pos, ActiveMenu menu) {
        super(slate, player, menu);
        this.item = item;
        this.pos = pos;
    }

    /**
     * Gets the ItemStack that was clicked.
     *
     * @return the clicked ItemStack
     */
    public ItemStack item() {
        return item;
    }

    /**
     * Gets the slot position that was clicked.
     *
     * @return the clicked slot
     */
    public SlotPos pos() {
        return pos;
    }
}
