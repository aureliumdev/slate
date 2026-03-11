package dev.aurelium.slate.fabric.function;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

@FunctionalInterface
public interface ItemMetaParser {

    /**
     * Handles parsing of a custom item meta key.
     *
     * @param item the item to parse
     * @param config the {@link ConfigurationNode} of the item section
     * @return the modified item
     */
    ItemStack parse(ItemStack item, ConfigurationNode config);

}
