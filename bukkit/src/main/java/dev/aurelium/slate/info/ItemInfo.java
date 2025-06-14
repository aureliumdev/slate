package dev.aurelium.slate.info;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents contextual information about an item in a menu.
 */
public class ItemInfo extends MenuInfo {

    private final ItemStack item;

    public ItemInfo(Slate slate, Player player, ActiveMenu menu, ItemStack item) {
        super(slate, player, menu);
        this.item = item;
    }

    public ItemStack item() {
        return item;
    }

}
