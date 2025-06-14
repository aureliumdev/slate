package dev.aurelium.slate.action;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.inv.content.InventoryContents;
import dev.aurelium.slate.menu.MenuInventory;
import org.bukkit.entity.Player;

public abstract class Action {

    protected final Slate slate;

    public Action(Slate slate) {
        this.slate = slate;
    }

    public abstract void execute(Player player, MenuInventory menuInventory, InventoryContents contents);

}
