package dev.aurelium.slate.action.parser;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.SoundAction;
import org.bukkit.SoundCategory;
import org.spongepowered.configurate.ConfigurationNode;

public class SoundActionParser extends ActionParser {

    public SoundActionParser(Slate slate) {
        super(slate);
    }

    @Override
    public Action parse(ConfigurationNode config) {
        var category = SoundCategory.valueOf(config.node("category").getString("master").toUpperCase());

        return new SoundAction(slate,
                config.node("sound").getString(),
                category,
                config.node("volume").getFloat(1f),
                config.node("pitch").getFloat(1f));
    }
}
