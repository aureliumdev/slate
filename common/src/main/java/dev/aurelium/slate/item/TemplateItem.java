package dev.aurelium.slate.item;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.context.ContextGroup;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.position.PositionProvider;
import dev.aurelium.slate.ref.ItemRef;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateItem<C> extends MenuItem {

    private final Class<C> contextClass;
    private final TemplateData<C> data;
    private final ItemRef defaultBaseItem;
    private final PositionProvider defaultPosition;
    private final Map<String, ContextGroup> contextGroups;

    public TemplateItem(SlateLibrary slate, String name, Class<C> contextClass, TemplateData<C> data, ItemRef defaultBaseItem,
                        String displayName, List<LoreLine> lore, ItemActions actions, ItemConditions conditions,
                        PositionProvider defaultPosition, Map<String, Object> options, Map<String, ContextGroup> contextGroups) {
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

    public Map<C, ItemRef> getBaseItems() {
        Map<C, ItemRef> clonedItems = new HashMap<>();
        for (Map.Entry<C, ItemRef> entry : data.baseItems().entrySet()) {
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
    public ItemRef getDefaultBaseItem() {
        if (defaultBaseItem != null) {
            return defaultBaseItem.clone();
        }
        return null;
    }

    @Nullable
    public PositionProvider getDefaultPosition() {
        return defaultPosition;
    }

    @Nullable
    public String getContextualDisplayName(C context) {
        return data.displayNames().get(context);
    }

    public TemplateData<C> getData() {
        return data;
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

}
