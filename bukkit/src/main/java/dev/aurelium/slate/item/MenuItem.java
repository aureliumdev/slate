package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.Condition;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.menu.MenuInventory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MenuItem {

    protected final Slate slate;
    private final String name;
    private final String displayName;
    private final List<LoreLine> lore;
    private final ItemActions actions;
    private final ItemConditions conditions;
    private final Map<String, Object> options;

    public MenuItem(Slate slate, String name, String displayName, List<LoreLine> lore, ItemActions actions,
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

    public boolean failsViewConditions(Player player, MenuInventory inventory) {
        return failsConditions(player, inventory, conditions.viewConditions());
    }

    public boolean failsClickConditions(ClickTrigger trigger, Player player, MenuInventory inventory) {
        return failsConditions(player, inventory, conditions.clickConditions().getOrDefault(trigger, new ArrayList<>()));
    }

    protected boolean failsConditions(Player player, MenuInventory inventory, List<Condition> conditions) {
        for (Condition condition : conditions) {
            if (!condition.isMet(player, inventory)) {
                return true;
            }
        }
        // Return true only if all conditions are met
        return false;
    }

}
