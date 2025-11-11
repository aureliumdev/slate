package dev.aurelium.slate.action.parser;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.MenuAction;
import dev.aurelium.slate.action.MenuAction.ActionType;
import dev.aurelium.slate.context.ContextProvider;
import dev.aurelium.slate.util.TextUtil;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MenuActionParser extends ActionParser {

    public MenuActionParser(Slate slate) {
        super(slate);
    }

    @Override
    public Action parse(ConfigurationNode config) {
        ActionType actionType = MenuAction.ActionType.valueOf(Objects.requireNonNull(config.node("action").getString()).toUpperCase(Locale.ROOT));
        String menuName = config.node("menu").getString();

        return new MenuAction(slate, actionType, menuName, getProperties(menuName, config));
    }

    public Map<String, Object> getProperties(String menuName, ConfigurationNode config) {
        ConfigurationNode propertiesConfig = config.node("properties");
        Map<String, Object> properties = new HashMap<>();
        for (Object keyObj : propertiesConfig.childrenMap().keySet()) {
            String key = String.valueOf(keyObj);
            Object valueObj = propertiesConfig.node(keyObj).raw();
            if (valueObj instanceof String value) {
                if (value.contains(":")) { // Parse custom object
                    String type = TextUtil.substringBefore(value, ":");
                    value = TextUtil.substringAfter(value, ":");
                    // Get context provider from type
                    ContextProvider<?> contextProvider = slate.getContextManager().getContextProvider(type);
                    if (contextProvider != null) {
                        properties.put(key, contextProvider.parse(menuName, value)); // Add the parsed object
                    }
                } else {
                    properties.put(key, value);
                }
            } else {
                properties.put(key, valueObj);
            }
        }
        return properties;
    }
}
