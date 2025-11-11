package dev.aurelium.slate.item;

import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.position.PositionProvider;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public record TemplateVariant<C>(
        @NonNull Set<C> contextFilters,
        @NonNull Map<String, Object> propertyFilters,
        @Nullable ItemStack baseItem,
        @Nullable PositionProvider position,
        @Nullable String displayName,
        @NonNull List<LoreLine> lore) {
}
