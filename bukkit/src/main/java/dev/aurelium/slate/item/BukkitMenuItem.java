package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.condition.BukkitConditionChecker;
import dev.aurelium.slate.action.condition.Condition;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.menu.MenuInventory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BukkitMenuItem {

    private final MenuItem menuItem;
    private final BukkitConditionChecker checker;

    public BukkitMenuItem(Slate slate, MenuItem menuItem) {
        this.menuItem = menuItem;
        this.checker = new BukkitConditionChecker(slate);
    }

    public boolean failsViewConditions(Player player, MenuInventory inventory) {
        return failsConditions(player, inventory, menuItem.getConditions().viewConditions());
    }

    public boolean failsClickConditions(ClickTrigger trigger, Player player, MenuInventory inventory) {
        return failsConditions(player, inventory, menuItem.getConditions().clickConditions().getOrDefault(trigger, new ArrayList<>()));
    }

    protected boolean failsConditions(Player player, MenuInventory inventory, List<Condition> conditions) {
        for (Condition condition : conditions) {
            if (!checker.isMet(condition, player, inventory)) {
                return true;
            }
        }
        // Return true only if all conditions are met
        return false;
    }

}
