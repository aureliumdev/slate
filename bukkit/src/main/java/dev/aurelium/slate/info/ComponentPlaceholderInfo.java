package dev.aurelium.slate.info;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.component.ComponentData;
import dev.aurelium.slate.item.provider.PlaceholderData;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

/**
 * Represents contextual information about a placeholder in a menu within a component.
 *
 * @param <T> the linked template context object type
 */
public class ComponentPlaceholderInfo<T> extends TemplatePlaceholderInfo<T> {

    private final ComponentData component;

    public ComponentPlaceholderInfo(Slate slate, Player player, String placeholder, ActiveMenu menu, PlaceholderData data, ComponentData component, T value) {
        super(slate, player, placeholder, menu, data, value);
        this.component = component;
    }

    /**
     * Gets the component data of the component containing the placeholder, which contains
     * information about this instance.
     *
     * @return the component data
     */
    public ComponentData component() {
        return component;
    }
}
