package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Stores contextual data for template clicks.
 *
 * @param <T> the template context type
 */
public class TemplateClick<T> extends ItemClick {

    private final T value;

    public TemplateClick(Slate slate, Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu menu, T value) {
        super(slate, player, event, item, pos, menu);
        this.value = value;
    }

    /**
     * Gets the context value corresponding to the instance of the template that was clicked.
     *
     * @return the context value
     */
    public T value() {
        return value;
    }
}
