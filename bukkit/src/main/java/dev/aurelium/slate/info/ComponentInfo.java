package dev.aurelium.slate.info;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ComponentInfo<T> extends TemplateInfo<T> {

    private final String parentName;

    public ComponentInfo(Slate slate, Player player, ActiveMenu menu, ItemStack item, String parentName, T value) {
        super(slate, player, menu, item, value);
        this.parentName = parentName;
    }

    /**
     * Gets the name of the item or template this component is part of.
     *
     * @return the parent item name
     */
    public String getParentName() {
        return parentName;
    }
}
