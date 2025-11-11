package dev.aurelium.slate.item.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.item.ItemVariant;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.SingleItem;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SingleItemBuilder extends MenuItemBuilder {

    private List<SlotPos> positions;
    private ItemStack baseItem;
    private List<ItemVariant> variants = new ArrayList<>();

    public SingleItemBuilder(Slate slate) {
        super(slate);
    }

    public SingleItemBuilder positions(List<SlotPos> positions) {
        this.positions = positions;
        return this;
    }

    public SingleItemBuilder baseItem(ItemStack baseItem) {
        this.baseItem = baseItem;
        return this;
    }

    public SingleItemBuilder variants(List<ItemVariant> variants) {
        this.variants = variants;
        return this;
    }

    @Override
    public MenuItem build() {
        return new SingleItem(slate, name, baseItem, displayName, lore, actions, conditions, positions, options, variants);
    }
}
