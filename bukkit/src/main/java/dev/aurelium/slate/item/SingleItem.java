package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.lore.LoreLine;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class SingleItem extends MenuItem {

    private final List<SlotPos> positions;
    private final ItemStack baseItem;
    private final List<ItemVariant> variants;

    public SingleItem(Slate slate, String name, ItemStack baseItem, String displayName, List<LoreLine> lore,
                      ItemActions actions, ItemConditions conditions, List<SlotPos> positions, Map<String, Object> options, List<ItemVariant> variants) {
        super(slate, name, displayName, lore, actions, conditions, options);
        this.positions = positions;
        this.baseItem = baseItem;
        this.variants = variants;
    }

    public List<SlotPos> getPositions() {
        return positions;
    }

    public ItemStack getBaseItem() {
        return baseItem.clone();
    }

    public List<ItemVariant> getVariants() {
        return variants;
    }
}
