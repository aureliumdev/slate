package dev.aurelium.slate.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.info.ItemInfo;
import dev.aurelium.slate.item.TemplateClick;
import dev.aurelium.slate.item.provider.PlaceholderType;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.text.TemplateTextReplacer;
import dev.aurelium.slate.function.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public record BuiltTemplate<T>(
        Class<T> contextType,
        Map<String, TemplateReplacer<T>> replacers,
        TemplateReplacer<T> anyReplacer,
        Map<ClickTrigger, TemplateClicker<T>> clickers,
        TemplateModifier<T> modifier,
        DefinedContexts<T> definedContexts,
        TemplateSlot<T> slotProvider,
        MenuListener initListener,
        ContextListener<T> contextListener
) {

    public static <T> BuiltTemplate<T> createEmpty(Class<T> contextType) {
        return new BuiltTemplate<>(contextType, new HashMap<>(), p -> null, new HashMap<>(), ItemInfo::item,
                m -> new HashSet<>(), t -> null, m -> {}, t -> {});
    }

    public String applyReplacers(String input, Slate slate, Player player, ActiveMenu activeMenu, PlaceholderType type, T value) {
        var replacer = new TemplateTextReplacer<>(slate, replacers, anyReplacer);
        return replacer.applyReplacers(input, player, activeMenu, type, value);
    }

    public void handleClick(Set<ClickTrigger> actions, TemplateClick<T> templateClick) {
        for (Entry<ClickTrigger, TemplateClicker<T>> entry : clickers.entrySet()) {
            if (actions.contains(entry.getKey())) { // Only click if click action matches a defined clicker
                entry.getValue().click(templateClick);
            }
        }
    }

}
