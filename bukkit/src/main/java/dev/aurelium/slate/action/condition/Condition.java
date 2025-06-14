package dev.aurelium.slate.action.condition;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.menu.MenuInventory;
import org.bukkit.entity.Player;

public abstract class Condition {

    protected final Slate slate;

    public Condition(Slate slate) {
        this.slate = slate;
    }

    public abstract boolean isMet(Player player, MenuInventory menuInventory);

}
