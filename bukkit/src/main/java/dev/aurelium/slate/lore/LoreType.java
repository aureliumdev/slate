package dev.aurelium.slate.lore;

import dev.aurelium.slate.lore.parser.ComponentLoreParser;
import dev.aurelium.slate.lore.parser.LoreParser;
import dev.aurelium.slate.lore.parser.TextLoreParser;

public enum LoreType {

    TEXT(TextLoreParser.class),
    COMPONENT(ComponentLoreParser.class);

    private final Class<? extends LoreParser> parserClass;

    LoreType(Class<? extends LoreParser> parserClass) {
        this.parserClass = parserClass;
    }

    public Class<? extends LoreParser> getParserClass() {
        return parserClass;
    }
}
