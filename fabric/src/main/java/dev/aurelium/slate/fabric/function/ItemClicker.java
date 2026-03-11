package dev.aurelium.slate.fabric.function;

import dev.aurelium.slate.fabric.item.ItemClick;

@FunctionalInterface
public interface ItemClicker {

    /**
     * Code to run when an item is clicked.
     *
     * @param info the {@link ItemClick} context object
     */
    void click(ItemClick info);

}
