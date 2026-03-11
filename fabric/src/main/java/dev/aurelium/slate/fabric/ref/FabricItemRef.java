package dev.aurelium.slate.fabric.ref;

import dev.aurelium.slate.ref.ItemRef;
import net.minecraft.world.item.ItemStack;

public class FabricItemRef implements ItemRef, Cloneable {

    private final ItemStack item;

    public FabricItemRef(ItemStack item) {
        this.item = item;
    }

    public static FabricItemRef wrap(ItemStack item) {
        return new FabricItemRef(item);
    }

    public static ItemStack unwrap(ItemRef ref) {
        if (ref == null) {
            return null;
        }
        return ((FabricItemRef) ref).get();
    }

    @Override
    public ItemStack get() {
        return item;
    }

    @Override
    public ItemRef clone() {
        try {
            return wrap(unwrap((ItemRef) super.clone()).copy());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
