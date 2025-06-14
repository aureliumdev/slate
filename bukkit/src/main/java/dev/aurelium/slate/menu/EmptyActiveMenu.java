package dev.aurelium.slate.menu;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.fill.FillData;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EmptyActiveMenu extends ActiveMenu {

    EmptyActiveMenu(Slate slate, Player player) {
        super(new MenuInventory(slate,
                new LoadedMenu("", "", 0, new HashMap<>(), new HashMap<>(), new HashMap<>(), FillData.empty(slate), new HashMap<>(), new HashMap<>()),
                player,
                new HashMap<>(),
                0));
    }

}
