package dev.aurelium.slate.lore.parser;

import dev.aurelium.slate.lore.LoreLine;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public interface LoreParser {

    LoreLine parse(ConfigurationNode config) throws SerializationException;

}
