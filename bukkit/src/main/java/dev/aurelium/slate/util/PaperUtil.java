package dev.aurelium.slate.util;

import dev.aurelium.slate.text.TextFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PaperUtil {

    public static final boolean IS_PAPER = init();

    private static boolean init() {
        return hasClass("com.destroystokyo.paper.PaperConfig") || hasClass("io.papermc.paper.configuration.Configuration");
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static void setDisplayName(ItemMeta meta, Component component) {
        if (IS_PAPER) {
            meta.setDisplayNameComponent(BungeeComponentSerializer.get().serialize(component));
        } else {
            meta.setDisplayName(new TextFormatter().toString(component));
        }
    }

    @SuppressWarnings("deprecation")
    public static void setLore(ItemMeta meta, List<Component> components) {
        if (IS_PAPER) {
            List<BaseComponent[]> bungee = new ArrayList<>();
            BungeeComponentSerializer serializer = BungeeComponentSerializer.get();
            for (Component component : components) {
                bungee.add(serializer.serialize(component));
            }
            meta.setLoreComponents(bungee);
        } else {
            List<String> text = new ArrayList<>();
            TextFormatter tf = new TextFormatter();
            for (Component component : components) {
                text.add(tf.toString(component));
            }
            meta.setLore(text);
        }
    }

}