package dev.aurelium.slate;

import dev.aurelium.slate.action.ActionManager;
import dev.aurelium.slate.builder.BuiltMenu;
import dev.aurelium.slate.builder.GlobalBehavior;
import dev.aurelium.slate.builder.GlobalBehaviorBuilder;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.context.ContextManager;
import dev.aurelium.slate.inv.InventoryManager;
import dev.aurelium.slate.item.ItemProtection;
import dev.aurelium.slate.menu.LoadedMenu;
import dev.aurelium.slate.menu.MenuFileGenerator;
import dev.aurelium.slate.menu.MenuLoader;
import dev.aurelium.slate.menu.MenuOpener;
import dev.aurelium.slate.option.SlateOptions;
import dev.aurelium.slate.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Slate {

    private final JavaPlugin plugin;
    private final Scheduler scheduler;
    private final ContextManager contextManager;
    private final InventoryManager inventoryManager;
    private final ActionManager actionManager;
    private final boolean placeholderAPIEnabled;
    private final SlateOptions options;
    private final Map<String, BuiltMenu> builtMenus = new HashMap<>();
    private final Map<String, LoadedMenu> loadedMenus = new LinkedHashMap<>();
    private final MenuOpener menuOpener = new MenuOpener(this);
    private final ItemProtection itemProtection = new ItemProtection();

    private GlobalBehavior globalBehavior = GlobalBehaviorBuilder.builder().build();

    public Slate(JavaPlugin plugin, SlateOptions options) {
        this.plugin = plugin;
        this.scheduler = Scheduler.createScheduler(plugin);
        this.contextManager = new ContextManager();
        this.inventoryManager = new InventoryManager(plugin, scheduler);
        inventoryManager.init();
        this.actionManager = new ActionManager(this);
        this.placeholderAPIEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        this.options = options;
        if (options.removalProtection()) {
            plugin.getServer().getPluginManager().registerEvents(itemProtection, plugin);
        }
    }

    /**
     * Loads all menu files in the mainDirectory defined in {@link SlateOptions} and uses any
     * mergeDirectories defined to merge menu files together before loading. Files are registered as
     * a {@link LoadedMenu} after loading.
     *
     * @return the number of menus successfully loaded
     */
    public int loadMenus() {
        MenuLoader loader = new MenuLoader(this, options.mainDirectory(), options.mergeDirectories());
        return loader.loadMenus();
    }

    /**
     * Generates missing menu files into the {@link SlateOptions#mainDirectory()} based on the menu names
     * registered previously through {@link #buildMenu(String, Consumer)}.
     */
    public void generateFiles() {
        new MenuFileGenerator(this).generate();
    }

    /**
     * Opens a menu for a player with defined properties and page.
     *
     * @param player the player to open the menu for
     * @param name the name of the menu to open
     * @param properties the properties to use in the menu
     * @param page the page to open the menu on, starts at 0
     */
    public void openMenu(Player player, String name, Map<String, Object> properties, int page) {
        menuOpener.openMenu(player, name, properties, page);
    }

    /**
     * Opens a menu for a player with defined properties and page without checking for exceptions
     * during opening. This means that if the menu throws an exception during opening, it will not
     * be automatically closed for the player. Useful for custom error logging.
     *
     * @param player the player to open the menu for
     * @param name the name of the menu to open
     * @param properties the properties to use in the menu
     * @param page the page to open the menu on, starts at 0
     */
    public void openMenuUnchecked(Player player, String name, Map<String, Object> properties, int page) {
        menuOpener.openMenuUnchecked(player, name, properties, page);
    }

    /**
     * Opens a menu for a player with defined properties.
     *
     * @param player the player to open the menu for
     * @param name the name of the menu to open
     * @param properties the properties to use in the menu
     */
    public void openMenu(Player player, String name, Map<String, Object> properties) {
        menuOpener.openMenu(player, name, properties);
    }

    /**
     * Opens a menu for a player with a defined page.
     *
     * @param player the player to open the menu for
     * @param name the name of the menu to open
     * @param page the page to open the menu on, starts at 0
     */
    public void openMenu(Player player, String name, int page) {
        menuOpener.openMenu(player, name, page);
    }

    /**
     * Opens a menu for a player.
     *
     * @param player the player to open the menu for
     * @param name the name of the menu to open
     */
    public void openMenu(Player player, String name) {
        menuOpener.openMenu(player, name);
    }

    public void unregisterMenus() {
        this.builtMenus.clear();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public ContextManager getContextManager() {
        return contextManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }

    public int getLoreWrappingWidth() {
        return options.loreWrappingWidth();
    }

    public SlateOptions getOptions() {
        return options;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public ItemProtection getItemProtection() {
        return itemProtection;
    }

    /**
     * Creates a new menu with a name and a consumer to build the menu.
     * This registers the menu in the backend to be used to define behavior for the frontend
     * menu file of the same name.
     *
     * @param name the name of the menu, must match the name of the menu file without the extension
     * @param menu the consumer to build the menu, best used as a lambda
     */
    public void buildMenu(String name, Consumer<MenuBuilder> menu) {
        MenuBuilder builder = MenuBuilder.builder();
        menu.accept(builder);
        builtMenus.put(name, builder.build());
    }

    /**
     * Gets a built menu by its name.
     *
     * @param name the name of the menu
     * @return the built menu, or an empty menu if the menu does not exist
     */
    @NotNull
    public BuiltMenu getBuiltMenu(String name) {
        return builtMenus.getOrDefault(name, BuiltMenu.createEmpty());
    }

    public Map<String, BuiltMenu> getBuiltMenus() {
        return builtMenus;
    }

    public void addLoadedMenu(LoadedMenu menu) {
        this.loadedMenus.put(menu.name(), menu);
    }

    @Nullable
    public LoadedMenu getLoadedMenu(String name) {
        return loadedMenus.get(name);
    }

    public Map<String, LoadedMenu> getLoadedMenus() {
        return loadedMenus;
    }

    public void setGlobalBehavior(Consumer<GlobalBehaviorBuilder> options) {
        GlobalBehaviorBuilder builder = GlobalBehaviorBuilder.builder();
        options.accept(builder);
        this.globalBehavior = builder.build();
    }

    public GlobalBehavior getGlobalBehavior() {
        return globalBehavior;
    }

    public void addMergeDirectory(File mergeDir) {
        if (!mergeDir.isDirectory()) return;

        if (options.mergeDirectories().contains(mergeDir)) {
            removeMergeDirectory(mergeDir);
        }
        options.mergeDirectories().add(mergeDir);
    }

    public void removeMergeDirectory(File mergeDir) {
        options.mergeDirectories().remove(mergeDir);
    }

}
