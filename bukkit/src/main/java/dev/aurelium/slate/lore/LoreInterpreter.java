package dev.aurelium.slate.lore;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.builder.BuiltComponent;
import dev.aurelium.slate.builder.BuiltItem;
import dev.aurelium.slate.builder.BuiltTemplate;
import dev.aurelium.slate.component.ComponentData;
import dev.aurelium.slate.component.MenuComponent;
import dev.aurelium.slate.info.ComponentInfo;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.TemplateItem;
import dev.aurelium.slate.item.provider.PlaceholderType;
import dev.aurelium.slate.lore.type.ComponentLore;
import dev.aurelium.slate.lore.type.TextLore;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.text.TextFormatter;
import dev.aurelium.slate.util.LoreUtil;
import dev.aurelium.slate.util.Pair;
import dev.aurelium.slate.util.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class LoreInterpreter {

    private final Slate slate;
    private final TextFormatter tf = new TextFormatter();

    public LoreInterpreter(Slate slate) {
        this.slate = slate;
    }

    @NotNull
    public List<Component> interpretLore(List<LoreLine> loreLines, Player player, ActiveMenu activeMenu, BuiltItem builtItem, MenuItem menuItem) {
        List<String> lore = new ArrayList<>();
        for (LoreLine line : loreLines) {
            if (line instanceof TextLore textLore) {
                lore.add(interpretTextLore(textLore, player, activeMenu, builtItem));
            } else if (line instanceof ComponentLore componentLore) {
                List<String> list = interpretComponent(componentLore, player, activeMenu, menuItem);
                if (list != null) {
                    lore.addAll(list);
                }
            }
        }
        lore = tf.applyNewLines(lore);
        return tf.toComponentLore(lore);
    }

    @NotNull
    public <T> List<Component> interpretLore(List<LoreLine> loreLines, Player player, ActiveMenu activeMenu, BuiltTemplate<T> builtTemplate, TemplateItem<T> templateItem, T context) {
        List<String> lore = new ArrayList<>();
        for (LoreLine line : loreLines) {
            if (line instanceof TextLore textLore) {
                lore.add(interpretTextLore(textLore, player, activeMenu, builtTemplate, context));
            } else if (line instanceof ComponentLore componentLore) {
                List<String> list = interpretComponent(componentLore, player, activeMenu, templateItem, context);
                if (list != null) {
                    lore.addAll(list);
                }
            }
        }
        lore = tf.applyNewLines(lore);
        return tf.toComponentLore(lore);
    }

    private String interpretTextLore(TextLore textLore, Player player, ActiveMenu activeMenu, BuiltItem builtItem) {
        String text = textLore.getText();
        text = builtItem.applyReplacers(text, slate, player, activeMenu, PlaceholderType.LORE);
        return replaceAndWrap(textLore, player, text);
    }

    private <T> String interpretTextLore(TextLore textLore, Player player, ActiveMenu activeMenu, BuiltTemplate<T> builtTemplate, T context) {
        String text = textLore.getText();
        text = builtTemplate.applyReplacers(text, slate, player, activeMenu, PlaceholderType.LORE, context);
        return replaceAndWrap(textLore, player, text);
    }

    private <T> String interpretTextLore(TextLore textLore, Player player, ActiveMenu activeMenu, ComponentData componentData, @NotNull BuiltComponent<T> builtComponent, T context) {
        String text = textLore.getText();
        text = builtComponent.applyReplacers(text, slate, player, activeMenu, componentData, context);
        return replaceAndWrap(textLore, player, text);
    }

    public static Pair<String, ListData> detectListPlaceholder(String placeholder) {
        if (!placeholder.endsWith("]") && !placeholder.endsWith(")")) {
            return new Pair<>(placeholder, new ListData(null, 0));
        }
        // Find the index of the opening bracket closest to the end of the placeholder
        int openBracket = placeholder.lastIndexOf("[");
        int closeBracket = placeholder.lastIndexOf("]");
        // No matching opening bracket
        if (openBracket == -1 || closeBracket == -1 || openBracket > closeBracket) {
            return new Pair<>(placeholder, new ListData(null, 0));
        }
        // Get the substring between the brackets
        String insert = placeholder.substring(openBracket + 1, closeBracket);
        // Find the index of the opening parenthesis closest to the end of the placeholder
        int openParen = placeholder.lastIndexOf("(");
        int closeParen = placeholder.lastIndexOf(")");
        // Find the interval between parenthesis
        int interval = 1;
        if (openParen != -1 && closeParen != -1 && openParen < closeParen) {
            String intervalString = placeholder.substring(openParen + 1, closeParen);
            try {
                interval = Integer.parseInt(intervalString);
            } catch (NumberFormatException ignored) {}
        }

        return new Pair<>(placeholder.substring(0, openBracket), new ListData(insert, interval));
    }

    @SuppressWarnings("unchecked")
    private <T> List<String> interpretComponent(ComponentLore lore, Player player, ActiveMenu activeMenu, TemplateItem<T> templateItem, T context) {
        // Choose the component if multiple
        String componentName = lore.getComponent();
        MenuComponent component = activeMenu.getComponents().get(componentName);
        if (component == null) {
            return null;
        }
        @NotNull BuiltComponent<T> builtComponent = (BuiltComponent<T>) slate.getBuiltMenu(activeMenu.getName()).components()
                .getOrDefault(componentName, BuiltComponent.createEmpty(component.contextClass()));
        ComponentInfo<T> info = new ComponentInfo<>(slate, player, activeMenu, new ItemStack(Material.STONE), templateItem.getName(), context);
        if (!builtComponent.visibility().shouldShow(info)) {
            return null;
        }
        // Get number of instances from provider or built component
        int instances = builtComponent.instances().getInstances(info);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < instances; i++) {
            ComponentData componentData = new ComponentData(i);
            // Interpret each line
            for (LoreLine line : component.lore()) {
                if (!(line instanceof TextLore)) { // Lines in a component must be TextLore
                    continue;
                }
                list.add(interpretTextLore((TextLore) line, player, activeMenu, componentData, builtComponent, context));
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private List<String> interpretComponent(ComponentLore lore, Player player, ActiveMenu activeMenu, MenuItem menuItem) {
        // Choose the component if multiple
        String componentName = lore.getComponent();
        MenuComponent component = activeMenu.getComponents().get(componentName);
        if (component == null) {
            return null;
        }
        @NotNull BuiltComponent<Object> builtComponent = (BuiltComponent<Object>) slate.getBuiltMenu(activeMenu.getName()).components()
                .getOrDefault(componentName, BuiltComponent.createEmpty(component.contextClass()));
        ComponentInfo<Object> info = new ComponentInfo<>(slate, player, activeMenu, new ItemStack(Material.STONE), menuItem.getName(), null);
        if (!builtComponent.visibility().shouldShow(info)) {
            return null;
        }

        // Get number of instances from provider or built component
        int instances = builtComponent.instances().getInstances(info);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < instances; i++) {
            ComponentData componentData = new ComponentData(i);
            // Interpret each line
            for (LoreLine line : component.lore()) {
                if (!(line instanceof TextLore)) { // Lines in a component must be TextLore
                    continue;
                }
                list.add(interpretTextLore((TextLore) line, player, activeMenu, componentData, builtComponent, null));
            }
        }
        return list;
    }

    private String replaceAndWrap(TextLore textLore, Player player, String text) {
        if (slate.isPlaceholderAPIEnabled()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        if (textLore.shouldWrap()) {
            if (textLore.isSmartWrap()) { // Detect tags inside string to use as insertions
                String firstStyle = textLore.getStyles().getStyle(textLore.getWrapStyle());
                AtomicReference<String> prevStyle = new AtomicReference<>(firstStyle);
                text = firstStyle + textLore.getWrapIndent() + LoreUtil.wrapLore(text, slate.getLoreWrappingWidth(), textLore, (line, lore) -> {
                    // Find the last style tag in the line
                    int lastStartIndex = 0;
                    int tagLength = 0;
                    for (int index : lore.getStyles().styleMap().keySet()) {
                        String tag = "<" + index + ">";
                        int startIndex = line.lastIndexOf(tag);
                        if (startIndex >= lastStartIndex) {
                            lastStartIndex = startIndex;
                            tagLength = tag.length();
                        }
                    }
                    String style;
                    if (tagLength > 0) {
                        style = line.substring(lastStartIndex, lastStartIndex + tagLength);
                        prevStyle.set(style);
                    } else {
                        style = prevStyle.get();
                    }
                    return "\n" + lore.getWrapIndent() + style;
                });
            } else { // Use the same string for all insertions
                String style = textLore.getStyles().getStyle(textLore.getWrapStyle());
                text = style + textLore.getWrapIndent() + LoreUtil.wrapLore(text, slate.getLoreWrappingWidth(), "\n" + textLore.getWrapIndent() + style);
            }
        }
        return applyStyleTags(textLore, text);
    }

    private String applyStyleTags(TextLore textLore, String text) {
        // Create a TagResolver for each style
        boolean[] usedTags = new boolean[10];
        for (Map.Entry<Integer, String> entry : textLore.getStyles().styleMap().entrySet()) {
            String target = String.valueOf(entry.getKey());
            String style = entry.getValue();
            String styleClose = TextUtil.replace(entry.getValue(), "<", "</"); // Convert style to closing tags

            text = TextUtil.replace(text, "<" + target + ">", style); // Replace opening tag
            text = TextUtil.replace(text, "</" + target + ">", styleClose); // Replacing closing tag

            // Mark as used
            int index = entry.getKey();
            if (index < 10) {
                usedTags[index] = true;
            }
        }
        // Remove unused tags
        for (int i = 0; i < usedTags.length; i++) {
            if (usedTags[i]) continue; // Ignore used tags
            text = TextUtil.replace(text, "<" + i + ">", "");
            text = TextUtil.replace(text, "</" + i + ">", "");
        }
        return text;
    }

}
