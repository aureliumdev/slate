package dev.aurelium.slate.item.parser;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.action.parser.MenuActionParser;
import dev.aurelium.slate.context.ContextGroup;
import dev.aurelium.slate.context.ContextProvider;
import dev.aurelium.slate.context.GroupAlign;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.TemplateVariant;
import dev.aurelium.slate.item.builder.TemplateItemBuilder;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.position.FixedPosition;
import dev.aurelium.slate.position.GroupPosition;
import dev.aurelium.slate.position.PositionProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;

public class TemplateItemParser<C> extends MenuItemParser {

    @NotNull
    private final ContextProvider<C> contextProvider;

    public TemplateItemParser(Slate slate, @NotNull ContextProvider<C> contextProvider) {
        super(slate);
        this.contextProvider = contextProvider;
    }

    @Override
    public MenuItem parse(ConfigurationNode section, String menuName) {
        TemplateItemBuilder<C> builder = new TemplateItemBuilder<>(slate);

        String name = (String) Objects.requireNonNull(section.key());
        builder.name(name);

        builder.contextClass(contextProvider.getType());

        Map<String, ContextGroup> groups = loadGroups(section);
        builder.contextGroups(groups);

        // Look through keys for contexts
        Map<C, ItemStack> baseItems = new HashMap<>();
        Map<C, PositionProvider> positions = new HashMap<>();

        Map<C, String> contextualDisplayNames = new HashMap<>();
        Map<C, List<LoreLine>> contextualLore = new HashMap<>();
        Map<C, ItemConditions> contextualConditions = new HashMap<>();
        Map<C, ItemActions> contextualActions = new HashMap<>();

        for (ConfigurationNode contextNode : section.node("contexts").childrenMap().values()) {
            String key;
            if (contextNode.key() instanceof String) {
                key = (String) Objects.requireNonNull(contextNode.key());
            } else {
                key = String.valueOf(contextNode.key());
            }

            C context = contextProvider.parse(menuName, key);

            if (context != null) { // Context parse found a match
                if (!contextNode.node("material").virtual() || !contextNode.node("key").virtual()) {
                    baseItems.put(context, itemParser.parseBaseItem(contextNode));
                }
                String positionString = contextNode.node("pos").getString();
                if (positionString != null) {
                    positions.put(context, new FixedPosition(parsePosition(positionString)));
                } else if (!contextNode.node("group").virtual()) {
                    String groupName = contextNode.node("group").getString();
                    ContextGroup group = groups.get(groupName);
                    if (group == null) {
                        positions.put(context, new FixedPosition(parsePosition("0,0")));
                    } else {
                        int order = contextNode.node("order").getInt(1);
                        positions.put(context, new GroupPosition(group, order));
                    }
                }
                // Parse contextual display name and lore
                String contextualDisplayName = itemParser.parseDisplayName(contextNode);
                if (contextualDisplayName != null) {
                    contextualDisplayNames.put(context, contextualDisplayName);
                }
                List<LoreLine> contextualLoreList = itemParser.parseLore(contextNode);
                if (!contextualLoreList.isEmpty()) {
                    contextualLore.put(context, contextualLoreList);
                }
                // Parse view_conditions, etc. on the specific context
                contextualConditions.put(context, getConditions(contextNode, menuName));

                contextualActions.put(context, parseActions(contextNode, menuName));
            }
        }

        builder.contextualDisplayNames(contextualDisplayNames);
        builder.contextualLore(contextualLore);
        builder.contextualConditions(contextualConditions);
        builder.contextualActions(contextualActions);

        String defaultPos = section.node("pos").getString();
        if (positions.isEmpty() && defaultPos != null) {
            SlotPos pos = parsePosition(defaultPos);
            builder.defaultPosition(pos);
        }

        builder.baseItems(baseItems);
        builder.positions(positions);
        builder.variants(parseVariants(section, menuName, groups));

        // Parse default base item if exists
        if (!section.node("material").virtual() || !section.node("key").virtual()) {
            builder.defaultBaseItem(itemParser.parseBaseItem(section));
        }

        parseCommonOptions(builder, section, menuName);

        return builder.build();
    }

    private List<TemplateVariant<C>> parseVariants(ConfigurationNode section, String menuName, Map<String, ContextGroup> groups) {
        List<TemplateVariant<C>> variants = new ArrayList<>();
        for (ConfigurationNode variantNode : section.node("variants").childrenList()) {
            Set<C> contextFilters = new HashSet<>();
            if (!variantNode.node("contexts").empty()) {
                variantNode.node("contexts").childrenList().stream()
                        .map(node -> contextProvider.parse(menuName, String.valueOf(node.raw())))
                        .filter(Objects::nonNull)
                        .forEach(contextFilters::add);
            } else {
                String key = variantNode.node("context").getString("");
                C context = contextProvider.parse(menuName, key);
                if (context != null) {
                    contextFilters.add(context);
                }
            }

            Map<String, Object> propertyFilters = new MenuActionParser(slate).getProperties(menuName, variantNode);

            ItemStack baseItem = parseVariantBaseItem(variantNode);
            PositionProvider positionProvider = parsePosition(variantNode, groups);
            String displayName = itemParser.parseDisplayName(variantNode);
            List<LoreLine> lore = itemParser.parseLore(variantNode);

            TemplateVariant<C> variant = new TemplateVariant<>(contextFilters, propertyFilters, baseItem, positionProvider, displayName, lore);
            variants.add(variant);
        }
        return variants;
    }

    private Map<String, ContextGroup> loadGroups(ConfigurationNode section) {
        // Load groups
        Map<String, ContextGroup> groups = new HashMap<>();
        for (ConfigurationNode groupNode : section.node("groups").childrenMap().values()) {
            String groupName = (String) groupNode.key();
            if (groupName == null) continue;

            SlotPos start = parsePosition(groupNode.node("start").getString("0,0"));
            SlotPos end = parsePosition(groupNode.node("end").getString("0,0"));
            GroupAlign align = GroupAlign.valueOf(groupNode.node("align").getString("CENTER").toUpperCase(Locale.ROOT));

            groups.put(groupName, new ContextGroup(start, end, align));
        }
        return groups;
    }

}
