package dev.aurelium.slate.component;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.lore.LoreFactory;
import dev.aurelium.slate.lore.LoreLine;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public class ComponentParser {

    private final Slate slate;

    public ComponentParser(Slate slate) {
        this.slate = slate;
    }

    public MenuComponent parse(ConfigurationNode config) {
        String contextString = config.node("context").getString();
        Class<?> contextClass = slate.getContextManager().getContextClass(contextString);

        List<LoreLine> lore = new LoreFactory(slate).getLore(config.node("lore"));

        return new MenuComponent(contextClass, lore);
    }

}
