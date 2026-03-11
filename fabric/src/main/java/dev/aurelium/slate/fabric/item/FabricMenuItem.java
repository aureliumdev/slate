package dev.aurelium.slate.fabric.item;

import dev.aurelium.slate.action.condition.Condition;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.action.condition.FabricConditionChecker;
import dev.aurelium.slate.fabric.inv.MenuInventory;
import dev.aurelium.slate.item.MenuItem;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class FabricMenuItem {

    private final MenuItem menuItem;
    private final FabricConditionChecker checker;

    public FabricMenuItem(Slate slate, MenuItem menuItem) {
        this.menuItem = menuItem;
        this.checker = new FabricConditionChecker(slate);
    }

    public boolean failsViewConditions(ServerPlayer player, MenuInventory inventory) {
        return failsConditions(player, inventory, menuItem.getConditions().viewConditions());
    }

    public boolean failsClickConditions(ClickTrigger trigger, ServerPlayer player, MenuInventory inventory) {
        return failsConditions(player, inventory, menuItem.getConditions().clickConditions().getOrDefault(trigger, new ArrayList<>()));
    }

    protected boolean failsConditions(ServerPlayer player, MenuInventory inventory, List<Condition> conditions) {
        for (Condition condition : conditions) {
            if (!checker.isMet(condition, player, inventory)) {
                return true;
            }
        }
        // Return true only if all conditions are met
        return false;
    }

}
