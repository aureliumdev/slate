package dev.aurelium.slate.item.parser;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.parser.MenuActionParser;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.item.ItemVariant;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.builder.SingleItemBuilder;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.util.Validate;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SingleItemParser extends MenuItemParser {

    public SingleItemParser(Slate slate) {
        super(slate);
    }

    @Override
    public MenuItem parse(ConfigurationNode section, String menuName) {
        SingleItemBuilder builder = new SingleItemBuilder(slate);

        String name = (String) Objects.requireNonNull(section.key());
        builder.name(name);
        builder.baseItem(itemParser.parseBaseItem(section));

        builder.positions(parsePosList(section));

        builder.variants(parseVariants(section, menuName));

        parseCommonOptions(builder, section, menuName);

        return builder.build();
    }

    private List<ItemVariant> parseVariants(ConfigurationNode section, String menuName) {
        List<ItemVariant> variants = new ArrayList<>();
        for (ConfigurationNode variantNode : section.node("variants").childrenList()) {
            Map<String, Object> propertyFilters = new MenuActionParser(slate).getProperties(menuName, variantNode);

            ItemStack baseItem = parseVariantBaseItem(variantNode);

            String displayName = itemParser.parseDisplayName(variantNode);
            List<LoreLine> lore = itemParser.parseLore(variantNode);

            List<SlotPos> pos = null;
            if (!variantNode.node("pos").empty()) {
                pos = parsePosList(variantNode);
            }

            ItemVariant variant = new ItemVariant(propertyFilters, baseItem, pos, displayName, lore);
            variants.add(variant);
        }
        return variants;
    }

    private List<SlotPos> parsePosList(ConfigurationNode section) {
        ConfigurationNode posNode = section.node("pos");
        if (posNode.isList()) { // Multiple positions
            List<SlotPos> positions = new ArrayList<>();
            // Parse each position and add to list
            for (ConfigurationNode entry : posNode.childrenList()) {
                String positionString = entry.getString();
                if (positionString == null) continue;

                positions.add(parsePosition(positionString));
            }
            return positions;
        } else { // Single position
            String positionString = posNode.getString();
            Validate.notNull(positionString, "Item must specify pos");
            return List.of(parsePosition(positionString));
        }
    }

}
