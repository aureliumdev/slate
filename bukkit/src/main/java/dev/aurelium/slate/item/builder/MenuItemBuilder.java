package dev.aurelium.slate.item.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.lore.LoreLine;

import java.util.List;
import java.util.Map;

public abstract class MenuItemBuilder {

    protected final Slate slate;
    protected String name;
    protected String displayName;
    protected List<LoreLine> lore;
    protected ItemActions actions;
    protected ItemConditions conditions;
    protected Map<String, Object> options;

    public MenuItemBuilder(Slate slate) {
        this.slate = slate;
    }

    public abstract MenuItem build();

    public MenuItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MenuItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public MenuItemBuilder lore(List<LoreLine> lore) {
        this.lore = lore;
        return this;
    }

    public MenuItemBuilder actions(ItemActions actions) {
        this.actions = actions;
        return this;
    }

    public MenuItemBuilder conditions(ItemConditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public MenuItemBuilder options(Map<String, Object> options) {
        this.options = options;
        return this;
    }

}
