package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.Condition;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.context.ContextGroup;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.menu.MenuInventory;
import dev.aurelium.slate.position.PositionProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateItem<C> extends MenuItem {

    private final Class<C> contextClass;
    private final TemplateData<C> data;
    private final ItemStack defaultBaseItem;
    private final SlotPos defaultPosition;
    private final Map<String, ContextGroup> contextGroups;

    public TemplateItem(Slate slate, String name, Class<C> contextClass, TemplateData<C> data, ItemStack defaultBaseItem,
                        String displayName, List<LoreLine> lore, ItemActions actions, ItemConditions conditions,
                        SlotPos defaultPosition, Map<String, Object> options, Map<String, ContextGroup> contextGroups) {
        super(slate, name, displayName, lore, actions, conditions, options);
        this.contextClass = contextClass;
        this.data = data;
        this.defaultBaseItem = defaultBaseItem;
        this.defaultPosition = defaultPosition;
        this.contextGroups = contextGroups;
    }

    public Class<C> getContextClass() {
        return contextClass;
    }

    public PositionProvider getPosition(C context) {
        return data.positions().get(context);
    }

    public Map<C, PositionProvider> getPositionsMap() {
        return data.positions();
    }

    public Map<C, ItemStack> getBaseItems() {
        Map<C, ItemStack> clonedItems = new HashMap<>();
        for (Map.Entry<C, ItemStack> entry : data.baseItems().entrySet()) {
            clonedItems.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedItems;
    }

    @Nullable
    public ItemActions getContextActions(C context) {
        return data.actions().get(context);
    }

    public List<TemplateVariant<C>> getVariants() {
        return data.variants();
    }

    @Nullable
    public ItemStack getDefaultBaseItem() {
        if (defaultBaseItem != null) {
            return defaultBaseItem.clone();
        }
        return null;
    }

    @Nullable
    public SlotPos getDefaultPosition() {
        return defaultPosition;
    }

    @Nullable
    public String getContextualDisplayName(C context) {
        return data.displayNames().get(context);
    }

    /**
     * Gets the active display name for the given context. If the context has a contextual display name, it will be returned.
     * Otherwise, the default display name will be returned.
     *
     * @param context The context
     * @return The active display name
     */
    @Nullable
    public String getActiveDisplayName(C context) {
        String contextualDisplayName = getContextualDisplayName(context);
        if (contextualDisplayName != null) {
            return contextualDisplayName;
        }
        return getDisplayName();
    }

    @Nullable
    public List<LoreLine> getContextualLore(C context) {
        return data.lore().get(context);
    }

    /**
     * Gets the active lore for the given context. If the context has contextual lore, it will be returned.
     * Otherwise, the default lore will be returned.
     *
     * @param context The context
     * @return The active lore
     */
    @Nullable
    public List<LoreLine> getActiveLore(C context) {
        List<LoreLine> contextualLore = getContextualLore(context);
        if (contextualLore != null) {
            return contextualLore;
        }
        return getLore();
    }

    public Map<String, ContextGroup> getContextGroups() {
        return contextGroups;
    }

    public boolean failsContextViewConditions(C context, Player player, MenuInventory inventory) {
        return failsConditions(player, inventory, data.conditions().getOrDefault(context, ItemConditions.empty()).viewConditions());
    }

    public boolean failsContextClickConditions(C context, ClickTrigger trigger, Player player, MenuInventory inventory) {
        List<Condition> conditions = data.conditions().getOrDefault(context, ItemConditions.empty()).clickConditions().getOrDefault(trigger, new ArrayList<>());
        return failsConditions(player, inventory, conditions);
    }
}
