package dev.aurelium.slate.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.function.ItemReplacer;
import dev.aurelium.slate.function.LocaleProvider;
import dev.aurelium.slate.info.PlaceholderInfo;
import dev.aurelium.slate.item.provider.PlaceholderData;
import dev.aurelium.slate.item.provider.PlaceholderType;
import dev.aurelium.slate.lore.ListData;
import dev.aurelium.slate.lore.LoreInterpreter;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.util.LoreUtil;
import dev.aurelium.slate.util.Pair;
import dev.aurelium.slate.util.TextUtil;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public record GlobalBehavior(
        Set<ItemReplacer> globalReplacers,
        LocaleProvider localeProvider
) {

    /**
     * Creates a new {@link GlobalBehaviorBuilder}.
     *
     * @return the builder
     */
    public static GlobalBehaviorBuilder builder() {
        return GlobalBehaviorBuilder.builder();
    }

    public String applyGlobalReplacers(String input, Slate slate, Player player, ActiveMenu activeMenu, PlaceholderType type) {
        Map<String, ItemReplacer> pageReplacers = slate.getBuiltMenu(activeMenu.getName()).pageReplacers();

        String[] placeholders = TextUtil.substringsBetween(input, "{", "}");
        if (placeholders != null) {
            String style = LoreUtil.getStyle(input);
            for (String placeholder : placeholders) {
                Pair<String, ListData> pair = LoreInterpreter.detectListPlaceholder(placeholder);
                PlaceholderData data = new PlaceholderData(type, style, pair.second());
                PlaceholderInfo info = new PlaceholderInfo(slate, player, pair.first(), activeMenu, data);

                // Apply global replacers
                for (ItemReplacer replacer : globalReplacers) {
                    String replaced = replacer.replace(info);
                    if (replaced != null) {
                        input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
                    }
                }
            }
        }

        // Apply page replacers
        String[] afterGlobalPlaceholders = TextUtil.substringsBetween(input, "{", "}");
        if (afterGlobalPlaceholders != null) {
            String style = LoreUtil.getStyle(input);
            for (String placeholder : afterGlobalPlaceholders) {
                Pair<String, ListData> pair = LoreInterpreter.detectListPlaceholder(placeholder);
                PlaceholderData data = new PlaceholderData(type, style, pair.second());
                PlaceholderInfo info = new PlaceholderInfo(slate, player, pair.first(), activeMenu, data);

                for (Entry<String, ItemReplacer> entry : pageReplacers.entrySet()) {
                    if (!entry.getKey().equals(pair.first())) continue;
                    // Replacer target string matches current placeholder name
                    String replaced = entry.getValue().replace(info);
                    if (replaced != null) {
                        input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
                    }
                }
            }
        }
        return input;
    }

}
