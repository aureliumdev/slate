package dev.aurelium.slate.fabric.item;

import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.menu.ActiveMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Stores contextual data for template clicks.
 *
 * @param <T> the template context type
 */
public class TemplateClick<T> extends ItemClick {

    private final T value;

    public TemplateClick(Slate slate, ServerPlayer player, ItemStack item, SlotPos pos, ActiveMenu menu, T value) {
        super(slate, player, item, pos, menu);
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
