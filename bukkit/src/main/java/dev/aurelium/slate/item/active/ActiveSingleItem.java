package dev.aurelium.slate.item.active;

import dev.aurelium.slate.item.SingleItem;

public class ActiveSingleItem extends ActiveItem {

    private final SingleItem item;

    public ActiveSingleItem(SingleItem item) {
        this.item = item;
    }

    public SingleItem getItem() {
        return item;
    }

}
