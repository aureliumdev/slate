package dev.aurelium.slate.function;

import dev.aurelium.slate.item.ItemClick;

@FunctionalInterface
public interface ItemClicker {

    /**
     * Code to run when an item is clicked.
     *
     * @param info the {@link ItemClick} context object
     */
    void click(ItemClick info);

}
