package dev.aurelium.slate.fabric.item.provider;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Function to get a custom item from a key.
 */
@FunctionalInterface
public interface KeyedItemProvider {

    /**
     * Gets a custom item from a key.
     *
     * @param key the key used in the menu file
     * @return the item stack
     */
    @Nullable ItemStack getItem(String key);

}
