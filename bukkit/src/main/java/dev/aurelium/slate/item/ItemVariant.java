package dev.aurelium.slate.item;

import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.lore.LoreLine;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record ItemVariant(@NonNull Map<String, Object> propertyFilters,
        @Nullable ItemStack baseItem,
        @Nullable List<SlotPos> positions,
        @Nullable String displayName,
        @NonNull List<LoreLine> lore) {
}
