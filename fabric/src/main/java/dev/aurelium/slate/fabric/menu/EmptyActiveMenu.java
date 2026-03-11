package dev.aurelium.slate.fabric.menu;

import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.fill.FabricFillData;
import dev.aurelium.slate.fabric.inv.MenuInventory;
import dev.aurelium.slate.menu.LoadedMenu;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

public class EmptyActiveMenu extends FabricActiveMenu {

    EmptyActiveMenu(Slate slate, ServerPlayer player) {
        super(new MenuInventory(slate,
                new LoadedMenu("", "", 0, new HashMap<>(), new HashMap<>(), new HashMap<>(), FabricFillData.empty(slate), new HashMap<>(), new HashMap<>()),
                player,
                new HashMap<>(),
                0));
    }
}
