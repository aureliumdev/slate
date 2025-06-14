package dev.aurelium.slate.action.condition;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.menu.MenuInventory;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.Map;

public class PlaceholderCondition extends Condition {

    private final String placeholder;
    private final String value;
    private final Compare compare;

    public PlaceholderCondition(Slate slate, String placeholder, String value, Compare compare) {
        super(slate);
        this.placeholder = placeholder;
        this.value = value;
        this.compare = compare;
    }

    @Override
    public boolean isMet(Player player, MenuInventory menuInventory) {
        String leftText = replaceProperties(placeholder, menuInventory);
        if (slate.isPlaceholderAPIEnabled()) {
            leftText = PlaceholderAPI.setPlaceholders(player, leftText);
        }
        String rightText = replaceProperties(value, menuInventory);
        if (slate.isPlaceholderAPIEnabled()) {
            rightText = PlaceholderAPI.setPlaceholders(player, rightText);
        }
        try {
            return compare.test(leftText, rightText);
        } catch (NumberFormatException e) {
            slate.getPlugin().getLogger().warning("Slate: Failed to evaluate placeholder condition in menu " + menuInventory.getMenu().name());
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

    public enum Compare {

        EQUALS((left, right) -> {
            try {
                double leftDouble = Double.parseDouble(left);
                double rightDouble = Double.parseDouble(right);
                return Double.compare(leftDouble, rightDouble) == 0;
            } catch (NumberFormatException e) {
                return left.equals(right);
            }
        }),
        GREATER_THAN((left, right) -> {
            double leftDouble = Double.parseDouble(left);
            double rightDouble = Double.parseDouble(right);
            return leftDouble > rightDouble;
        }),
        GREATER_THAN_OR_EQUALS((left, right) -> {
            double leftDouble = Double.parseDouble(left);
            double rightDouble = Double.parseDouble(right);
            return leftDouble >= rightDouble;
        }),
        LESS_THAN((left, right) -> {
            double leftDouble = Double.parseDouble(left);
            double rightDouble = Double.parseDouble(right);
            return leftDouble < rightDouble;
        }),
        LESS_THAN_OR_EQUALS((left, right) -> {
            double leftDouble = Double.parseDouble(left);
            double rightDouble = Double.parseDouble(right);
            return leftDouble <= rightDouble;
        });

        private final ComparisonTest test;

        Compare(ComparisonTest test) {
            this.test = test;
        }

        public boolean test(String left, String right) throws NumberFormatException {
            return test.test(left, right);
        }

    }

    interface ComparisonTest {

        boolean test(String left, String right);

    }

}
