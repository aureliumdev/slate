package dev.aurelium.slate.fabric.info;

import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.menu.ActiveMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Represents contextual information about an item in a menu.
 */
public class ItemInfo extends MenuInfo {

    private final ItemStack item;

    public ItemInfo(Slate slate, ServerPlayer player, ActiveMenu menu, ItemStack item) {
        super(slate, player, menu);
        this.item = item;
    }

    public ItemStack item() {
        return item;
    }

}
