package dev.aurelium.slate.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.function.ItemClicker;
import dev.aurelium.slate.function.ItemModifier;
import dev.aurelium.slate.function.ItemReplacer;
import dev.aurelium.slate.function.MenuListener;
import dev.aurelium.slate.info.ItemInfo;
import dev.aurelium.slate.info.PlaceholderInfo;
import dev.aurelium.slate.item.ItemClick;
import dev.aurelium.slate.item.provider.PlaceholderData;
import dev.aurelium.slate.item.provider.PlaceholderType;
import dev.aurelium.slate.lore.ListData;
import dev.aurelium.slate.lore.LoreInterpreter;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.util.LoreUtil;
import dev.aurelium.slate.util.Pair;
import dev.aurelium.slate.util.TextUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public record BuiltItem(
        Map<String, ItemReplacer> replacers,
        ItemReplacer anyReplacer,
        Map<ClickTrigger, ItemClicker> clickers,
        @NotNull ItemModifier modifier,
        MenuListener initListener
) {

    public static BuiltItem createEmpty() {
        return new BuiltItem(new HashMap<>(), p -> null, new HashMap<>(), ItemInfo::item, m -> {});
    }

    public String applyReplacers(String input, Slate slate, Player player, ActiveMenu activeMenu, PlaceholderType type) {
        input = slate.getGlobalBehavior().applyGlobalReplacers(input, slate, player, activeMenu, type);
        // Detect placeholders
        String[] placeholders = TextUtil.substringsBetween(input, "{", "}");
        if (placeholders != null) {
            String style = LoreUtil.getStyle(input);
            for (String placeholder : placeholders) {
                // Get list data
                Pair<String, ListData> pair = LoreInterpreter.detectListPlaceholder(placeholder);
                PlaceholderData data = new PlaceholderData(type, style, pair.second());

                String coreName = pair.first(); // The name of the placeholder without list data formatting
                PlaceholderInfo info = new PlaceholderInfo(slate, player, coreName, activeMenu, data);

                // Apply single replacers
                for (Entry<String, ItemReplacer> entry : replacers.entrySet()) {
                    if (!entry.getKey().equals(coreName)) continue;
                    // Replacer target string matches current placeholder name
                    String replaced = entry.getValue().replace(info);
                    if (replaced != null) {
                        input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
                    }
                }
                // Apply anyReplacer
                String replaced = anyReplacer.replace(info);
                if (replaced != null) {
                    input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
                }
            }
        }
        return input;
    }

    public void handleClick(Set<ClickTrigger> actions, ItemClick itemClick) {
        for (Entry<ClickTrigger, ItemClicker> entry : clickers.entrySet()) {
            if (actions.contains(entry.getKey())) { // Only click if click action matches a defined clicker
                entry.getValue().click(itemClick);
            }
        }
    }

}
