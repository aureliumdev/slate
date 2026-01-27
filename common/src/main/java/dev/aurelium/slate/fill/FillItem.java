package dev.aurelium.slate.fill;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.ref.ItemRef;

import java.util.HashMap;

public class FillItem extends MenuItem {

    private final ItemRef baseItem;

    public FillItem(SlateLibrary slate, ItemRef baseItem) {
        super(slate, "fill", " ", null, ItemActions.empty(), ItemConditions.empty(), new HashMap<>());
        this.baseItem = baseItem;
    }

    public ItemRef getBaseItem() {
        return baseItem;
    }

}
