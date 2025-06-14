package dev.aurelium.slate.function;

import dev.aurelium.slate.info.TemplateInfo;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface TemplateModifier<T> {

    /**
     * Modifies the item of a template instance before it is displayed. The original item can be accessed with {@link TemplateInfo#item()}.
     *
     * @param info the {@link TemplateInfo} context object
     * @return the modified item
     */
    ItemStack modify(TemplateInfo<T> info);

}
