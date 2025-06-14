package dev.aurelium.slate.lore;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.lore.parser.LoreParser;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoreFactory {

    private final Slate slate;

    public LoreFactory(Slate slate) {
        this.slate = slate;
    }

    public List<LoreLine> getLore(ConfigurationNode config) {
        List<LoreLine> lines = new ArrayList<>();
        if (!config.isList()) {
            return lines;
        }
        for (ConfigurationNode lineNode : config.childrenList()) {
            LoreLine loreLine = buildLoreLine(lineNode);
            if (loreLine != null) {
                lines.add(loreLine);
            }
        }
        return lines;
    }

    @Nullable
    @SuppressWarnings("deprecation")
    private LoreLine buildLoreLine(ConfigurationNode config) {
        try {
            LoreType type = getType(config);

            Class<? extends LoreParser> parserClass = type.getParserClass();

            LoreParser parser = parserClass.newInstance();
            return parser.parse(config);
        } catch (Exception e) {
            slate.getPlugin().getLogger().warning("Error building lore line: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private LoreType getType(ConfigurationNode config) {
        if (!config.isMap()) {
            return LoreType.TEXT;
        }
        LoreType type;
        // Check for an explicit type
        String typeExplicit = config.node("type").getString();
        if (typeExplicit != null) {
            type = LoreType.valueOf(typeExplicit.toUpperCase(Locale.ROOT));
        } else if (!config.node("text").virtual()) { // Auto-detect type
            type = LoreType.TEXT;
        } else if (!config.node("component").virtual()) {
            type = LoreType.COMPONENT;
        } else {
            throw new IllegalArgumentException("Line does not define an explicit type or contain auto-detectable keys");
        }

        return type;
    }

}
