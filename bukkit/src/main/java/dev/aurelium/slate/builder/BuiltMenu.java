package dev.aurelium.slate.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.info.PlaceholderInfo;
import dev.aurelium.slate.item.provider.PlaceholderData;
import dev.aurelium.slate.item.provider.PlaceholderType;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.util.LoreUtil;
import dev.aurelium.slate.util.TextUtil;
import dev.aurelium.slate.function.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public record BuiltMenu(
        Map<String, BuiltItem> items,
        Map<String, BuiltTemplate<?>> templates,
        Map<String, BuiltComponent<?>> components,
        Map<String, ItemReplacer> pageReplacers,
        Map<String, ItemReplacer> titleReplacers,
        ItemReplacer titleAnyReplacer,
        PageProvider pageProvider,
        PropertyProvider propertyProvider,
        ItemModifier fillItem,
        MenuListener openListener,
        MenuListener updateListener,
        Map<String, Object> defaultOptions
) {

    public static BuiltMenu createEmpty() {
        return new BuiltMenu(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), p -> null, m -> 1,
                m -> new HashMap<>(), i -> null, m -> {}, m -> {}, new HashMap<>());
    }

    // Also gets items in name(1) format
    @NotNull
    public BuiltItem getBackingItem(String name) {
        BuiltItem direct = items.get(name);
        if (direct != null) {
            return direct;
        }
        if (isDuplicateItemName(name)) {
            String backingName = name.substring(0, name.lastIndexOf("("));
            BuiltItem backing = items.get(backingName);
            if (backing != null) {
                return backing;
            }
        }
        return BuiltItem.createEmpty();
    }

    @SuppressWarnings("unchecked")
    public <T> BuiltTemplate<T> getTemplate(String name, Class<T> contextType) {
        BuiltTemplate<?> template = templates.get(name);
        if (template != null && template.contextType().equals(contextType)) {
            return (BuiltTemplate<T>) template;
        }
        return BuiltTemplate.createEmpty(contextType);
    }

    @SuppressWarnings("unchecked")
    public <T> BuiltComponent<T> getComponent(String name, Class<T> contextType) {
        BuiltComponent<?> component = components.get(name);
        if (component != null && component.contextType().equals(contextType)) {
            return (BuiltComponent<T>) component;
        }
        return BuiltComponent.createEmpty(contextType);
    }

    public String applyTitleReplacers(String input, Slate slate, Player player, ActiveMenu activeMenu) {
        PlaceholderType type = PlaceholderType.TITLE;
        input = slate.getGlobalBehavior().applyGlobalReplacers(input, slate, player, activeMenu, type);
        for (Entry<String, ItemReplacer> entry : titleReplacers.entrySet()) {
            String placeholder = entry.getKey();
            PlaceholderData data = new PlaceholderData(type, LoreUtil.getStyle(input), null);

            String replaced = entry.getValue().replace(new PlaceholderInfo(slate, player, placeholder, activeMenu, data));
            if (replaced != null) {
                input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
            }
        }
        // Detect placeholders and replace with anyReplacer
        String[] placeholders = TextUtil.substringsBetween(input, "{", "}");
        if (placeholders != null) {
            PlaceholderData data = new PlaceholderData(type, LoreUtil.getStyle(input), null);
            for (String placeholder : placeholders) {
                String replaced = titleAnyReplacer.replace(new PlaceholderInfo(slate, player, placeholder, activeMenu, data));
                if (replaced != null) {
                    input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
                }
            }
        }
        return input;
    }

    private boolean isDuplicateItemName(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        int len = str.length();
        int i = len - 1;

        // Check for closing parenthesis
        if (str.charAt(i) != ')') {
            return false;
        }
        i--;

        // Check for at least one digit
        if (i < 0 || !Character.isDigit(str.charAt(i))) {
            return false;
        }

        // Skip all digits
        while (i >= 0 && Character.isDigit(str.charAt(i))) {
            i--;
        }

        // Check for opening parenthesis
        return i >= 0 && str.charAt(i) == '(';
    }

}
