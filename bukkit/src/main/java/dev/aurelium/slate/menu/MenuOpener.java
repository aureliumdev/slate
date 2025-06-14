package dev.aurelium.slate.menu;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.builder.BuiltMenu;
import dev.aurelium.slate.inv.InventoryListener;
import dev.aurelium.slate.inv.SmartInventory;
import dev.aurelium.slate.text.TextFormatter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;

public class MenuOpener {

    private final Slate slate;
    private final TextFormatter tf = new TextFormatter();

    public MenuOpener(Slate slate) {
        this.slate = slate;
    }

    /**
     * Opens a loaded menu for a player, will fail silently if the menu does not exist.
     *
     * @param player The player to display the menu to
     * @param name The name of the menu
     * @param properties Map of menu properties
     * @param page The page number to open, 0 is the first page
     */
    public void openMenu(Player player, String name, Map<String, Object> properties, int page) {
        slate.getScheduler().run(player, () -> {
            try {
                openMenuUnchecked(player, name, properties, page);
            } catch (Exception e) {
                player.closeInventory();
                slate.getPlugin().getLogger().warning("Error opening Slate menu " + name);
                e.printStackTrace();
            }
        });
    }

    public void openMenuUnchecked(Player player, String name, Map<String, Object> properties, int page) {
        LoadedMenu menu = slate.getLoadedMenu(name);
        if (menu == null) {
            throw new IllegalArgumentException("Menu with name " + name + " not registered");
        }
        MenuInventory menuInventory = new MenuInventory(slate, menu, player, properties, page);
        String title = menu.title();
        // Apply BuiltMenu replacers
        BuiltMenu builtMenu = slate.getBuiltMenu(name);
        title = builtMenu.applyTitleReplacers(title, slate, player, menuInventory.getActiveMenu());

        if (slate.isPlaceholderAPIEnabled()) {
            title = PlaceholderAPI.setPlaceholders(player, title);
        }

        if ((boolean) menu.options().getOrDefault("format_title", true)) {
            title = tf.toString(tf.toComponent(title));
        }

        // Build inventory and open
        SmartInventory smartInventory = SmartInventory.builder()
                .title(title)
                .size(menu.size(), 9)
                .manager(slate.getInventoryManager())
                .provider(menuInventory)
                .listener(new InventoryListener<>(InventoryCloseEvent.class, menuInventory::close))
                .build();
        smartInventory.open(player);
    }

    public void openMenu(Player player, String name, Map<String, Object> properties) {
        openMenu(player, name, properties, 0);
    }

    /**
     * Opens a loaded menu for a player, will fail silently if the menu does not exist.
     *
     * @param player The player to display the menu to
     * @param name The name of the menu
     * @param page The page number to open, 0 is the first page
     */
    public void openMenu(Player player, String name, int page) {
        openMenu(player, name, new HashMap<>(), page);
    }

    /**
     * Opens a loaded menu for a player, will fail silently if the menu does not exist.
     * Shows the first page.
     *
     * @param player The player to display the menu to
     * @param name The name of the menu
     */
    public void openMenu(Player player, String name) {
        openMenu(player, name, new HashMap<>(), 0);
    }

}
