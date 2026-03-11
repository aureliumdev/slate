package dev.aurelium.slate.fabric.info;

import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.menu.ActiveMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ComponentInfo<T> extends TemplateInfo<T> {

    private final String parentName;

    public ComponentInfo(Slate slate, ServerPlayer player, ActiveMenu menu, ItemStack item, String parentName, T value) {
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
