package dev.aurelium.slate.fill;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.item.parser.ItemParser;
import dev.aurelium.slate.item.parser.MenuItemParser;
import org.spongepowered.configurate.ConfigurationNode;

public class FillItemParser extends MenuItemParser {

    public FillItemParser(SlateLibrary slate, ItemParser parser) {
        super(slate, parser);
    }

    @Override
    public FillItem parse(ConfigurationNode section, String menuName) {
        if (!section.node("material").virtual()) {
            return new FillItem(slate, itemParser.parseBaseItem(section));
        } else {
            return null;
        }
    }
}
