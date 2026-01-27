package dev.aurelium.slate.item;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.lore.LoreLine;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class MenuItem {

    protected final SlateLibrary slate;
    private final String name;
    private final String displayName;
    private final List<LoreLine> lore;
    private final ItemActions actions;
    private final ItemConditions conditions;
    private final Map<String, Object> options;

    public MenuItem(SlateLibrary slate, String name, String displayName, List<LoreLine> lore, ItemActions actions,
                    ItemConditions conditions, Map<String, Object> options) {
        this.slate = slate;
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
        this.actions = actions;
        this.conditions = conditions;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public List<LoreLine> getLore() {
        return lore;
    }

    public ItemActions getActions() {
        return actions;
    }

    public ItemConditions getConditions() {
        return conditions;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

}
