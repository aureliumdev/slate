package dev.aurelium.slate.fabric.menu;

import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.trigger.MenuTrigger;
import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.action.FabricActionExecutor;
import dev.aurelium.slate.fabric.inv.MenuInventory;
import dev.aurelium.slate.menu.LoadedMenu;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class FabricLoadedMenu {

    public static void executeActions(LoadedMenu loadedMenu, Slate slate, MenuTrigger trigger, ServerPlayer player, MenuInventory menuInventory) {
        FabricActionExecutor actionExecutor = new FabricActionExecutor(slate);
        List<Action> actionList = loadedMenu.actions().getOrDefault(trigger, new ArrayList<>());
        for (Action action : actionList) {
            actionExecutor.executeAction(action, player, menuInventory);
        }
    }

}
