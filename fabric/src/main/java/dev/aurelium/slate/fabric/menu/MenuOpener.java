package dev.aurelium.slate.fabric.menu;

import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.builder.BuiltMenu;
import dev.aurelium.slate.fabric.inv.MenuInventory;
import dev.aurelium.slate.fabric.inv.SlateInventory;
import dev.aurelium.slate.fabric.text.TextFormatter;
import dev.aurelium.slate.menu.LoadedMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class MenuOpener {

    private final Slate slate;
    private final TextFormatter tf = new TextFormatter();

    public MenuOpener(Slate slate) {
        this.slate = slate;
    }

    public void openMenu(ServerPlayer player, String name, Map<String, Object> properties, int page) {
        slate.getScheduler().executeSync(() -> {
            try {
                openMenuUnchecked(player, name, properties, page);
            } catch (Exception e) {
                player.closeContainer();
                e.printStackTrace();
            }
        });
    }

    public void openMenuUnchecked(ServerPlayer player, String name, Map<String, Object> properties, int page) {
        LoadedMenu menu = slate.getLoadedMenu(name);
        if (menu == null) {
            throw new IllegalArgumentException("Menu with name " + name + " not registered");
        }
        MenuInventory menuInventory = new MenuInventory(slate, menu, player, properties, page);
        String title = menu.title();
        BuiltMenu builtMenu = slate.getBuiltMenu(name);
        title = builtMenu.applyTitleReplacers(title, slate, player, menuInventory.getActiveMenu());

        Component titleComponent;
        if ((boolean) menu.options().getOrDefault("format_title", true)) {
            titleComponent = slate.getAudiences().asNative(tf.toComponent(title));
        } else {
            titleComponent = Component.literal(title);
        }

        SlateInventory slateInventory = new SlateInventory(slate, slate.getInventoryManager(), menuInventory, titleComponent, menu.size());
        slateInventory.open(player);
    }
}
