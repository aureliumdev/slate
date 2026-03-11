package dev.aurelium.slate.fabric.function;

import dev.aurelium.slate.fabric.info.TemplateInfo;
import dev.aurelium.slate.inv.content.SlotPos;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TemplateSlot<T> {

    /**
     * Gets the slot position of a template instance.
     *
     * @param info the {@link TemplateInfo} context object
     * @return the slot position, or null if the instance should not be displayed
     */
    @Nullable
    SlotPos get(TemplateInfo<T> info);

}
