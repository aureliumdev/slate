package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.info.MenuInfo;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Stores contextual data for when a player clicks an item.
 */
public class ItemClick extends MenuInfo {

    private final InventoryClickEvent event;
    private final ItemStack item;
    private final SlotPos pos;

    public ItemClick(Slate slate, Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu menu) {
        super(slate, player, menu);
        this.event = event;
        this.item = item;
        this.pos = pos;
    }

    /**
     * Gets the original Bukkit event for the inventory click.
     *
     * @return the InventoryClickEvent
     */
    public InventoryClickEvent event() {
        return event;
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
