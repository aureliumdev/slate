package dev.aurelium.slate.text;

import dev.aurelium.slate.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextFormatter {

    private final String[] LEGACY_CODES = {
            "&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9",
            "&a", "&b", "&c", "&d", "&e", "&f",
            "&k", "&l", "&m", "&n", "&o", "&r"
    };
    private final String[] MINI_MESSAGE_CODES = {
            "<black>", "<dark_blue>", "<dark_green>", "<dark_aqua>", "<dark_red>", "<dark_purple>", "<gold>", "<gray>", "<dark_gray>", "<blue>",
            "<green>", "<aqua>", "<red>", "<light_purple>", "<yellow>", "<white>",
            "<obfuscated>", "<bold>", "<strikethrough>", "<underline>", "<italic>", "<reset>"
    };

    public Component toComponent(String message) {
        message = TextUtil.replace(message, "§", "&"); // Replace section symbols to allow MiniMessage parsing
        message = legacyCodesToMiniMessage(message);
        MiniMessage mm = MiniMessage.miniMessage();
        try {
            return removeItalic(mm.deserialize(message));
        } catch (ParsingException e) {
            Bukkit.getLogger().info("[Slate] Error applying MiniMessage formatting to input message: " + message);
            e.printStackTrace();
        }
        // MiniMessage parsing
        return removeItalic(Component.text(message));
    }

    public List<Component> toComponentLore(List<String> lore) {
        List<Component> componentLore = new ArrayList<>();
        for (String line : lore) {
            componentLore.add(toComponent(line));
        }
        return componentLore;
    }

    public Component removeItalic(Component component) {
        return Component.empty().style(Style.style(TextDecoration.ITALIC.withState(false))).append(component);
    }

    public String toString(Component component) {
        String message = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build()
                .serialize(component);
        message = TextUtil.replaceNonEscaped(message, "&", "§");
        return message;
    }

    public List<String> applyNewLines(List<String> input) {
        List<String> lore = new ArrayList<>();
        for (String entry : input) {
            lore.addAll(Arrays.asList(entry.split("(\\u005C\\u006E)|(\\n)")));
        }
        return lore;
    }

    public List<String> applyNewLines(String input) {
        return new ArrayList<>(Arrays.asList(input.split("(\\u005C\\u006E)|(\\n)")));
    }

    public String legacyCodesToMiniMessage(String input) {
        return TextUtil.replaceEach(input, LEGACY_CODES, MINI_MESSAGE_CODES);
    }

}
