package dev.aurelium.slate.item;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.ref.ItemRef;

import java.util.List;
import java.util.Map;

public class SingleItem extends MenuItem {

    private final List<SlotPos> positions;
    private final ItemRef baseItem;
    private final List<ItemVariant> variants;

    public SingleItem(SlateLibrary slate, String name, ItemRef baseItem, String displayName, List<LoreLine> lore,
                      ItemActions actions, ItemConditions conditions, List<SlotPos> positions, Map<String, Object> options, List<ItemVariant> variants) {
        super(slate, name, displayName, lore, actions, conditions, options);
        this.positions = positions;
        this.baseItem = baseItem;
        this.variants = variants;
    }

    public List<SlotPos> getPositions() {
        return positions;
    }

    public ItemRef getBaseItem() {
        return baseItem;
    }

    public List<ItemVariant> getVariants() {
        return variants;
    }
}
