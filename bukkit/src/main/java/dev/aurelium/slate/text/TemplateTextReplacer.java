package dev.aurelium.slate.text;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.function.TemplateReplacer;
import dev.aurelium.slate.info.TemplatePlaceholderInfo;
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

public class TemplateTextReplacer<T> {

    private final Slate slate;
    private final Map<String, TemplateReplacer<T>> replacers;
    private final TemplateReplacer<T> anyReplacer;

    public TemplateTextReplacer(Slate slate, Map<String, TemplateReplacer<T>> replacers, TemplateReplacer<T> anyReplacer) {
        this.slate = slate;
        this.replacers = replacers;
        this.anyReplacer = anyReplacer;
    }

    public String applyReplacers(String input, Player player, ActiveMenu activeMenu, PlaceholderType type, T value) {
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
                TemplatePlaceholderInfo<T> info = new TemplatePlaceholderInfo<>(slate, player, coreName, activeMenu, data, value);

                // Apply single replacers
                for (Entry<String, TemplateReplacer<T>> entry : replacers.entrySet()) {
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

}
