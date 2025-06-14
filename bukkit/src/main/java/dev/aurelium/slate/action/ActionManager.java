package dev.aurelium.slate.action;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.parser.CommandActionParser;
import dev.aurelium.slate.action.parser.MenuActionParser;
import dev.aurelium.slate.action.parser.SoundActionParser;
import dev.aurelium.slate.util.MapParser;
import dev.aurelium.slate.util.YamlLoader;
import org.spongepowered.configurate.ConfigurationNode;

import javax.annotation.Nullable;
import java.util.*;

public class ActionManager extends MapParser {

    private final Slate slate;

    public ActionManager(Slate slate) {
        this.slate = slate;
    }

    public List<Action> parseActions(ConfigurationNode config, String menuName) {
        List<Action> actions = new ArrayList<>();
        int index = 0;
        for (ConfigurationNode actionNode : config.childrenList()) {
            try {
                String type = Objects.requireNonNull(detectType(actionNode));
                Action action = switch (type) {
                    case "command" -> new CommandActionParser(slate).parse(actionNode);
                    case "menu" -> new MenuActionParser(slate).parse(actionNode);
                    case "sound" -> new SoundActionParser(slate).parse(actionNode);
                    default -> throw new IllegalArgumentException("Action with type " + type + " not found");
                };
                actions.add(action);
            } catch (RuntimeException e) {
                slate.getPlugin().getLogger().warning("Error parsing action in menu " + menuName + " at path " + YamlLoader.toDotString(config.path()) + ".[" + index + "], see below for error:");
                e.printStackTrace();
            }
            index++;
        }
        return actions;
    }

    @Nullable
    private String detectType(ConfigurationNode node) {
        String type = node.node("type").getString();
        if (type != null) {
            return type.toLowerCase(Locale.ROOT);
        }
        // Auto detection
        if (!node.node("command").virtual()) {
            return "command";
        } else if (!node.node("menu").virtual()) {
            return "menu";
        } else if (!node.node("sound").virtual()) {
            return "sound";
        }
        return null;
    }

}
