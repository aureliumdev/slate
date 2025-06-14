package dev.aurelium.slate.item;

import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.position.PositionProvider;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public record TemplateData<C>(
        Map<C, PositionProvider> positions,
        Map<C, ItemStack> baseItems,
        Map<C, String> displayNames,
        Map<C, List<LoreLine>> lore,
        Map<C, ItemConditions> conditions,
        Map<C, ItemActions> actions,
        List<TemplateVariant<C>> variants
) {

}
