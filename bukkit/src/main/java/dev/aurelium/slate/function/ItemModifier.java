package dev.aurelium.slate.function;

import dev.aurelium.slate.info.ItemInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemModifier {

    /**
     * Modifies an item before it is displayed. The original item can be accessed with {@link ItemInfo#item()}.
     *
     * @param info the {@link ItemInfo} context object
     * @return the modified item, or null if the item should not be displayed
     */
    @Nullable
    ItemStack modify(ItemInfo info);

}
