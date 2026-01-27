package dev.aurelium.slate.fill;

import dev.aurelium.slate.inv.content.SlotPos;
import org.jetbrains.annotations.Nullable;

public record FillData(FillItem item, SlotPos[] slots, boolean enabled) {

    @Override
    @Nullable
    public SlotPos[] slots() {
        return slots;
    }

}
