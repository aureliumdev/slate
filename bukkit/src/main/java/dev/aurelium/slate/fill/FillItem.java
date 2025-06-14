package dev.aurelium.slate.fill;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.item.MenuItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class FillItem extends MenuItem {

    private final ItemStack baseItem;

    public FillItem(Slate slate, ItemStack baseItem) {
        super(slate, "fill", " ", null, ItemActions.empty(), ItemConditions.empty(), new HashMap<>());
        this.baseItem = baseItem;
    }

    public ItemStack getBaseItem() {
        return baseItem;
    }

    public static FillItem getDefault(Slate slate) {
        return new FillItem(slate, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    }

}
