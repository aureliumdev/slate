package dev.aurelium.slate.lore.parser;

import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.lore.LoreStyles;
import dev.aurelium.slate.lore.type.TextLore;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Map;

public class TextLoreParser implements LoreParser {

    @Override
    public LoreLine parse(ConfigurationNode config) {
        // Raw String value
        if (!config.isMap()) {
            String text = config.getString("");
            return new TextLore(text, new LoreStyles(new HashMap<>()), false, 0, false, "");
        }
        // Map values
        String text = config.node("text").getString("");
        LoreStyles styles = parseStyles(config);
        boolean wrap = config.node("wrap").getBoolean(false);
        int wrapStyle = config.node("wrap_style").getInt(0);
        boolean smartWrap = config.node("smart_wrap").getBoolean(true);
        String wrapIndent = config.node("wrap_indent").getString("");

        return new TextLore(text, styles, wrap, wrapStyle, smartWrap, wrapIndent);
    }

    private LoreStyles parseStyles(ConfigurationNode config) {
        // Single style
        if (!config.node("style").virtual()) {
            String style = config.node("style").getString("");
            Map<Integer, String> styleMap = new HashMap<>();
            styleMap.put(0, style); // Set as style 0
            return new LoreStyles(styleMap);
        } else if (config.node("styles").isMap()) { // Multiple styles
            Map<Integer, String> styleMap = new HashMap<>();
            for (ConfigurationNode styleNode : config.node("styles").childrenMap().values()) {
                Object key = styleNode.key();
                int index;
                if (key instanceof String) {
                    index = Integer.parseInt((String) key);
                } else if (key instanceof Integer) {
                    index = (int) key;
                } else {
                    continue;
                }
                String style = styleNode.getString();
                if (style == null) continue;

                styleMap.put(index, style);
            }
            return new LoreStyles(styleMap);
        } else {
            return new LoreStyles(new HashMap<>()); // Return empty style map
        }
    }

}
