package dev.aurelium.slate.action.condition;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.action.condition.PlaceholderCondition.Compare;
import dev.aurelium.slate.menu.MenuInventory;
import org.bukkit.entity.Player;

import java.util.Map;

import static dev.aurelium.slate.bukkit.ref.BukkitPlayerRef.wrap;

public class BukkitConditionChecker {

    private final SlateLibrary slate;

    public BukkitConditionChecker(SlateLibrary slate) {
        this.slate = slate;
    }

    public boolean isMet(Condition condition, Player player, MenuInventory inventory) {
        if (condition instanceof PermissionCondition permissionCondition) {
            return checkPermission(permissionCondition, player);
        } else if (condition instanceof PlaceholderCondition placeholderCondition) {
            return checkPlaceholder(placeholderCondition, player, inventory);
        }
        return true;
    }

    private boolean checkPermission(PermissionCondition condition, Player player) {
        return player.hasPermission(condition.getPermission()) == condition.getValue();
    }

    private boolean checkPlaceholder(PlaceholderCondition condition, Player player, MenuInventory menuInventory) {
        String placeholder = condition.getPlaceholder();
        String value = condition.getValue();
        Compare compare = condition.getCompare();
        String leftText = replaceProperties(placeholder, menuInventory);
        if (slate.isPlaceholderAPIEnabled()) {
            leftText = slate.getPlaceholderHook().setPlaceholders(wrap(player), leftText);
        }
        String rightText = replaceProperties(value, menuInventory);
        if (slate.isPlaceholderAPIEnabled()) {
            rightText = slate.getPlaceholderHook().setPlaceholders(wrap(player), rightText);
        }
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
