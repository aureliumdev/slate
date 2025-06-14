package dev.aurelium.slate.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class ItemProtection implements Listener {

    private static final NamespacedKey KEY = Objects.requireNonNull(NamespacedKey.fromString("slate:menu_item"));

    public static ItemStack addProtection(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            var container = meta.getPersistentDataContainer();
            container.set(KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isProtected(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            var container = meta.getPersistentDataContainer();
            return container.has(KEY, PersistentDataType.BYTE);
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemSwitch(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int slot = event.getNewSlot();

        removeIfProtected(player, slot);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        if (action == InventoryAction.NOTHING || action == InventoryAction.UNKNOWN) {
            return;
        }

        if (event.getClickedInventory() instanceof PlayerInventory && event.getWhoClicked() instanceof Player player) {
            removeIfProtected(player, event.getSlot());
        }
    }

    private void removeIfProtected(Player player, int slot) {
        ItemStack item = player.getInventory().getItem(slot);
        if (item != null && isProtected(item)) {
            player.getInventory().setItem(slot, new ItemStack(Material.AIR));
        }
    }

}
