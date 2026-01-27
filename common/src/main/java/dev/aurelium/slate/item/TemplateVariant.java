package dev.aurelium.slate.item;

import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.position.PositionProvider;
import dev.aurelium.slate.ref.ItemRef;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record TemplateVariant<C>(
        @NonNull Set<C> contextFilters,
        @NonNull Map<String, Object> propertyFilters,
        @Nullable ItemRef baseItem,
        @Nullable PositionProvider position,
        @Nullable String displayName,
        @NonNull List<LoreLine> lore) {
}
