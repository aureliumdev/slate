package dev.aurelium.slate.item.active;

import dev.aurelium.slate.item.TemplateItem;

public class ActiveTemplateItem<C> extends ActiveItem {

    private final TemplateItem<C> item;

    public ActiveTemplateItem(TemplateItem<C> item) {
        this.item = item;
    }

    public TemplateItem<C> getItem() {
        return item;
    }

}
