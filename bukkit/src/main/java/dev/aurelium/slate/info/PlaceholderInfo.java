package dev.aurelium.slate.info;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.item.provider.PlaceholderData;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

/**
 * Represents contextual information about a placeholder in a menu.
 */
public class PlaceholderInfo extends MenuInfo {

    private final String placeholder;
    private final PlaceholderData data;

    public PlaceholderInfo(Slate slate, Player player, String placeholder, ActiveMenu menu, PlaceholderData data) {
        super(slate, player, menu);
        this.placeholder = placeholder;
        this.data = data;
    }

    /**
     * Gets the name of the placeholder string being replaced. This is the placeholder string in the menu file without
     * the curly braces.
     *
     * @return the placeholder
     */
    public String placeholder() {
        return placeholder;
    }

    /**
     * Gets data about the specific placeholder, such as where it is located (display name or lore),
     * the active style, and list data if applicable.
     *
     * @return the placeholder data
     */
    public PlaceholderData data() {
        return data;
    }
}
