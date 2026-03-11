package dev.aurelium.slate.fabric.builder;

import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.function.*;
import dev.aurelium.slate.fabric.info.ItemInfo;
import dev.aurelium.slate.fabric.item.TemplateClick;
import dev.aurelium.slate.fabric.text.TemplateTextReplacer;
import dev.aurelium.slate.item.provider.PlaceholderType;
import dev.aurelium.slate.menu.ActiveMenu;
import net.minecraft.server.level.ServerPlayer;

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

    public String applyReplacers(String input, Slate slate, ServerPlayer player, ActiveMenu activeMenu, PlaceholderType type, T value) {
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
