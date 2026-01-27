package dev.aurelium.slate.menu;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.BukkitActionExecutor;
import dev.aurelium.slate.action.trigger.MenuTrigger;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BukkitLoadedMenu {

    public static void executeActions(LoadedMenu loadedMenu, Slate slate, MenuTrigger trigger, Player player, MenuInventory menuInventory) {
        BukkitActionExecutor actionExecutor = new BukkitActionExecutor(slate);
        List<Action> actionList = loadedMenu.actions().getOrDefault(trigger, new ArrayList<>());
        for (Action action : actionList) {
            actionExecutor.executeAction(action, player, menuInventory);
        }
    }

}
