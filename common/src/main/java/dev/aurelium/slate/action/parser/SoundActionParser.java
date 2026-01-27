package dev.aurelium.slate.action.parser;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.SoundAction;
import net.kyori.adventure.sound.Sound.Source;
import org.spongepowered.configurate.ConfigurationNode;

public class SoundActionParser extends ActionParser {

    public SoundActionParser(SlateLibrary slate) {
        super(slate);
    }

    @Override
    public Action parse(ConfigurationNode config) {
        var category = Source.valueOf(config.node("category").getString("master").toUpperCase());

        return new SoundAction(
                config.node("sound").getString(),
                category,
                config.node("volume").getFloat(1f),
                config.node("pitch").getFloat(1f));
    }
}
