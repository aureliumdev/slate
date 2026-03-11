package dev.aurelium.slate.fabric.info;

import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.item.provider.PlaceholderData;
import dev.aurelium.slate.menu.ActiveMenu;
import net.minecraft.server.level.ServerPlayer;
/**
 * Represents contextual information about a placeholder in a menu within a template.
 *
 * @param <T> the template context object type
 */
public class TemplatePlaceholderInfo<T> extends PlaceholderInfo {

    private final T value;

    public TemplatePlaceholderInfo(Slate slate, ServerPlayer player, String placeholder, ActiveMenu menu, PlaceholderData data, T value) {
        super(slate, player, placeholder, menu, data);
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
