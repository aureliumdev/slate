package dev.aurelium.slate.action.condition;

import dev.aurelium.slate.SlateLibrary;

public class PermissionCondition extends Condition {

    private final String permission;
    private final boolean value;

    public PermissionCondition(SlateLibrary slate, String permission, boolean value) {
        super(slate);
        this.permission = permission;
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public String getPermission() {
        return permission;
    }
}
