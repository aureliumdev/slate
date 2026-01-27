package dev.aurelium.slate.action.parser;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.util.MapParser;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class ActionParser extends MapParser {

    protected final SlateLibrary slate;

    public ActionParser(SlateLibrary slate) {
        this.slate = slate;
    }

    public abstract Action parse(ConfigurationNode config);

}
