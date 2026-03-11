package dev.aurelium.slate.fabric.info;

import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.menu.ActiveMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Represents contextual information about an instance of a template in a menu with a specific context.
 *
 * @param <T> the context object type
 */
public class TemplateInfo<T> extends ItemInfo {

    private final T value;

    public TemplateInfo(Slate slate, ServerPlayer player, ActiveMenu menu, ItemStack item, T value) {
        super(slate, player, menu, item);
        this.value = value;
    }

    /**
     * Gets the value of the context of this template instance.
     *
     * @return the context object
     */
    public T value() {
        return value;
    }
}
