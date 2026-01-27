package dev.aurelium.slate.action.parser;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.action.CommandAction;
import dev.aurelium.slate.action.Action;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Locale;
import java.util.Objects;

public class CommandActionParser extends ActionParser {

    public CommandActionParser(SlateLibrary slate) {
        super(slate);
    }

    @Override
    public Action parse(ConfigurationNode config) {
        return new CommandAction(Objects.requireNonNull(config.node("command").getString()),
                CommandAction.Executor.valueOf(config.node("executor").getString("console").toUpperCase(Locale.ROOT)));
    }
}
