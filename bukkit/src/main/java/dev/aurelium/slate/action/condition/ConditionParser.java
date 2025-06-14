package dev.aurelium.slate.action.condition;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.condition.PlaceholderCondition.Compare;
import dev.aurelium.slate.util.YamlLoader;
import org.spongepowered.configurate.ConfigurationNode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ConditionParser {

    private final Slate slate;

    public ConditionParser(Slate slate) {
        this.slate = slate;
    }

    public List<Condition> parseConditions(ConfigurationNode config, String menuName) {
        List<Condition> conditions = new ArrayList<>();
        int index = 0;
        for (ConfigurationNode condNode : config.childrenList()) {
            try {
                String type = Objects.requireNonNull(detectType(condNode));
                if (type.equals("permission")) {
                    conditions.add(parsePermissionCondition(condNode));
                } else if (type.equals("placeholder")) {
                    conditions.add(parsePlaceholderCondition(condNode));
                }
            } catch (RuntimeException e) {
                slate.getPlugin().getLogger().warning("Error parsing condition in menu " + menuName + " at path " + YamlLoader.toDotString(config.path()) + ".[" + index + "], see below for error:");
                e.printStackTrace();
            }
            index++;
        }
        return conditions;
    }

    private PermissionCondition parsePermissionCondition(ConfigurationNode config) {
        return new PermissionCondition(slate,
                Objects.requireNonNull(config.node("permission").getString()),
                config.node("value").getBoolean(true));
    }

    private PlaceholderCondition parsePlaceholderCondition(ConfigurationNode config) {
        return new PlaceholderCondition(slate,
                Objects.requireNonNull(config.node("placeholder").getString()),
                Objects.requireNonNull(config.node("value").getString()),
                Compare.valueOf(config.node("compare").getString("equals").toUpperCase(Locale.ROOT)));
    }

    @Nullable
    private String detectType(ConfigurationNode node) {
        String type = node.node("type").getString();
        if (type != null) {
            return type.toLowerCase(Locale.ROOT);
        }
        // Auto detection
        if (!node.node("permission").virtual()) {
            return "permission";
        } else if (!node.node("placeholder").virtual()) {
            return "placeholder";
        }
        return null;
    }

}
