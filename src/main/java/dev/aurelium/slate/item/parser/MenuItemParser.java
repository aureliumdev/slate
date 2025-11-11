package dev.aurelium.slate.item.parser;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.ItemActions;
import dev.aurelium.slate.action.condition.Condition;
import dev.aurelium.slate.action.condition.ConditionParser;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.context.ContextGroup;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.builder.MenuItemBuilder;
import dev.aurelium.slate.menu.MenuLoader;
import dev.aurelium.slate.position.FixedPosition;
import dev.aurelium.slate.position.GroupPosition;
import dev.aurelium.slate.position.PositionProvider;
import dev.aurelium.slate.util.MapParser;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class MenuItemParser extends MapParser {

    protected final Slate slate;
    protected final ConfigurateItemParser itemParser;

    public MenuItemParser(Slate slate) {
        this.slate = slate;
        this.itemParser = new ConfigurateItemParser(slate);
    }

    public abstract MenuItem parse(ConfigurationNode config, String menuName);

    protected SlotPos parsePosition(String input) {
        String[] splitInput = input.split(",", 2);
        if (splitInput.length == 2) {
            int row = Integer.parseInt(splitInput[0]);
            int column = Integer.parseInt(splitInput[1]);
            return SlotPos.of(row, column);
        } else {
            int slot = Integer.parseInt(input);
            int row = slot / 9;
            int column = slot % 9;
            return SlotPos.of(row, column);
        }
    }

    protected @Nullable PositionProvider parsePosition(ConfigurationNode variantNode, Map<String, ContextGroup> groups) {
        String positionString = variantNode.node("pos").getString();
        if (positionString != null) {
            return new FixedPosition(parsePosition(positionString));
        } else if (!variantNode.node("group").virtual()) {
            String groupName = variantNode.node("group").getString();
            ContextGroup group = groups.get(groupName);
            if (group == null) {
                return new FixedPosition(parsePosition("0,0"));
            } else {
                int order = variantNode.node("order").getInt(1);
                return new GroupPosition(group, order);
            }
        }
        return null;
    }

    protected @Nullable ItemStack parseVariantBaseItem(ConfigurationNode variantNode) {
        if (!variantNode.node("material").virtual() || !variantNode.node("key").virtual()) {
            return itemParser.parseBaseItem(variantNode);
        }
        return null;
    }

    protected void parseCommonOptions(MenuItemBuilder builder, ConfigurationNode config, String menuName) {
        builder.displayName(itemParser.parseDisplayName(config));
        builder.lore(itemParser.parseLore(config));

        builder.actions(parseActions(config, menuName));
        builder.conditions(getConditions(config, menuName));

        builder.options(MenuLoader.loadOptions(config));
    }

    protected ItemActions parseActions(ConfigurationNode config, String menuName) {
        Map<ClickTrigger, List<Action>> actions = new LinkedHashMap<>();
        for (ClickTrigger clickTrigger : ClickTrigger.values()) {
            String id = clickTrigger.getId();
            if (!config.node(id).virtual()) {
                List<Action> clickActions = slate.getActionManager().parseActions(config.node(id), menuName);
                actions.put(clickTrigger, clickActions);
            }
        }
        return new ItemActions(actions);
    }

    protected ItemConditions getConditions(ConfigurationNode config, String menuName) {
        return new ItemConditions(parseViewConditions(config, menuName), parseClickConditions(config, menuName));
    }

    private List<Condition> parseViewConditions(ConfigurationNode config, String menuName) {
        ConfigurationNode vcNode = config.node("view_conditions");
        if (!vcNode.virtual()) {
            return new ConditionParser(slate).parseConditions(vcNode, menuName);
        }
        return new ArrayList<>();
    }

    private Map<ClickTrigger, List<Condition>> parseClickConditions(ConfigurationNode config, String menuName) {
        Map<ClickTrigger, List<Condition>> clickConditions = new LinkedHashMap<>();
        for (ClickTrigger clickTrigger : ClickTrigger.values()) {
            String id = clickTrigger.getId();
            // Ex: on_click_conditions
            String condListKey = id + "_conditions";
            if (!config.node(condListKey).virtual()) {
                List<Condition> conditionList = new ConditionParser(slate).parseConditions(config.node(condListKey), menuName);
                clickConditions.put(clickTrigger, conditionList);
            }
        }
        return clickConditions;
    }

}
