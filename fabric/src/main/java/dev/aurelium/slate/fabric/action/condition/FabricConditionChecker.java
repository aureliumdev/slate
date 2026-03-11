package dev.aurelium.slate.fabric.action.condition;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.action.condition.Condition;
import dev.aurelium.slate.action.condition.PermissionCondition;
import dev.aurelium.slate.action.condition.PlaceholderCondition;
import dev.aurelium.slate.action.condition.PlaceholderCondition.Compare;
import dev.aurelium.slate.fabric.inv.MenuInventory;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;


public class FabricConditionChecker {

    private final SlateLibrary slate;

    public FabricConditionChecker(SlateLibrary slate) {
        this.slate = slate;
    }

    public boolean isMet(Condition condition, ServerPlayer player, MenuInventory inventory) {
        if (condition instanceof PermissionCondition permissionCondition) {
            return checkPermission(permissionCondition, player);
        } else if (condition instanceof PlaceholderCondition placeholderCondition) {
            return checkPlaceholder(placeholderCondition, player, inventory);
        }
        return true;
    }

    private boolean checkPermission(PermissionCondition condition, ServerPlayer player) {
        // TODO implement LP hook
        return true;
    }

    private boolean checkPlaceholder(PlaceholderCondition condition, ServerPlayer player, MenuInventory menuInventory) {
        String placeholder = condition.getPlaceholder();
        String value = condition.getValue();
        Compare compare = condition.getCompare();
        String leftText = replaceProperties(placeholder, menuInventory);
        String rightText = replaceProperties(value, menuInventory);
        try {
            return compare.test(leftText, rightText);
        } catch (NumberFormatException e) {
            slate.getLogger().warning("Slate: Failed to evaluate placeholder condition in menu " + menuInventory.getMenu().name());
            e.printStackTrace();
            return false;
        }
    }

    private String replaceProperties(String text, MenuInventory menu) {
        for (Map.Entry<String, Object> property : menu.getProperties().entrySet()) {
            String key = property.getKey();
            Object value = property.getValue();
            text = text.replace("{" + key + "}", value.toString());
        }
        return text;
    }
}
