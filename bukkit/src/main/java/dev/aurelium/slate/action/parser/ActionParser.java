package dev.aurelium.slate.action.parser;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.util.MapParser;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class ActionParser extends MapParser {

    protected final Slate slate;

    public ActionParser(Slate slate) {
        this.slate = slate;
    }

    public abstract Action parse(ConfigurationNode config);

}
