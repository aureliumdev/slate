package dev.aurelium.slate.action.condition;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.menu.MenuInventory;
import org.bukkit.entity.Player;

public class PermissionCondition extends Condition {

    private final String permission;
    private final boolean value;

    public PermissionCondition(Slate slate, String permission, boolean value) {
        super(slate);
        this.permission = permission;
        this.value = value;
    }

    @Override
    public boolean isMet(Player player, MenuInventory menuInventory) {
        return player.hasPermission(permission) == value;
    }
}
