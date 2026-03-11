package dev.aurelium.slate.fabric;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.fabric.builder.BuiltMenu;
import dev.aurelium.slate.fabric.builder.GlobalBehavior;
import dev.aurelium.slate.fabric.builder.GlobalBehaviorBuilder;
import dev.aurelium.slate.fabric.builder.MenuBuilder;
import dev.aurelium.slate.fabric.hooks.FabricPlaceholderHook;
import dev.aurelium.slate.fabric.inv.InventoryManager;
import dev.aurelium.slate.fabric.menu.FabricMenuLoader;
import dev.aurelium.slate.fabric.menu.MenuFileGenerator;
import dev.aurelium.slate.fabric.menu.MenuOpener;
import dev.aurelium.slate.fabric.option.SlateOptions;
import dev.aurelium.slate.fabric.scheduler.FabricScheduler;
import dev.aurelium.slate.fabric.util.FabricLoggerAdapter;
import dev.aurelium.slate.hooks.PlaceholderHook;
import dev.aurelium.slate.menu.LoadedMenu;
import dev.aurelium.slate.menu.MenuLoader;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class Slate extends SlateLibrary {

    private final SlateOptions options;
    private final InventoryManager inventoryManager;
    private final Map<String, BuiltMenu> builtMenus = new HashMap<>();
    private final Map<String, LoadedMenu> loadedMenus = new LinkedHashMap<>();
    private final MinecraftServer server;
    private final FabricLoggerAdapter logger = new FabricLoggerAdapter();
    private final ClassLoader classLoader;
    private final FabricPlaceholderHook placeholderHook = new FabricPlaceholderHook();
    private final FabricScheduler scheduler;
    private final MinecraftServerAudiences audiences;
    private final MenuOpener menuOpener;

    private GlobalBehavior globalBehavior = GlobalBehaviorBuilder.builder().build();

    public Slate(MinecraftServer server, SlateOptions options) {
        this.server = server;
        this.inventoryManager = new InventoryManager(this);
        this.options = options;
        this.classLoader = this.getClass().getClassLoader();
        this.scheduler = new FabricScheduler();
        this.audiences = MinecraftServerAudiences.of(server);
        this.menuOpener = new MenuOpener(this);
    }

    public MinecraftServer getServer() {
        return server;
    }

    public SlateOptions getOptions() {
        return options;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
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

    public void openMenu(ServerPlayer player, String name, Map<String, Object> properties, int page) {
        menuOpener.openMenu(player, name, properties, page);
    }

    public void openMenu(ServerPlayer player, String name, Map<String, Object> properties) {
        menuOpener.openMenu(player, name, properties, 0);
    }

    public void openMenu(ServerPlayer player, String name) {
        menuOpener.openMenu(player, name, new HashMap<>(), 0);
    }

    @Override
    public int loadMenus() {
        MenuLoader loader = new FabricMenuLoader(this, options.mainDirectory(), options.mergeDirectories());
        return loader.loadMenus();
    }

    @Override
    public void generateFiles() {
        new MenuFileGenerator(this).generate();
    }

    @Override
    public void unregisterMenus() {
        this.builtMenus.clear();
    }

    @Override
    public int getLoreWrappingWidth() {
        return options.loreWrappingWidth();
    }

    @Override
    public void addMergeDirectory(File mergeDir) {
        if (!mergeDir.isDirectory()) return;

        if (options.mergeDirectories().contains(mergeDir)) {
            removeMergeDirectory(mergeDir);
        }
        options.mergeDirectories().add(mergeDir);
    }

    @Override
    public void removeMergeDirectory(File mergeDir) {
        options.mergeDirectories().remove(mergeDir);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public boolean isPlaceholderAPIEnabled() {
        return false;
    }

    @Override
    public File getDataFolder() {
        return options.mainDirectory().getParentFile();
    }

    @Override
    public void saveResource(String path, boolean replace) {
        File pluginFolder = getDataFolder();
        InputStream in = getResource(path);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + path + "' cannot be found in the mod jar");
        }

        File outFile = new File(pluginFolder, path);
        int lastIndex = path.lastIndexOf('/');
        File outDir = new File(pluginFolder, path.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            if (!outDir.mkdirs()) {
                throw new RuntimeException("Failed to make config dir");
            }
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                logger.warning("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            logger.severe("Could not save " + outFile.getName() + " to " + outFile);
        }
    }

    public InputStream getResource(String path) {
        try {
            URL url = classLoader.getResource(path);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public void addLoadedMenu(LoadedMenu menu) {
        loadedMenus.put(menu.name(), menu);
    }

    @Override
    public PlaceholderHook getPlaceholderHook() {
        return placeholderHook;
    }

    @Nullable
    @Override
    public LoadedMenu getLoadedMenu(String name) {
        return loadedMenus.get(name);
    }

    @Override
    public Map<String, LoadedMenu> getLoadedMenus() {
        return loadedMenus;
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

    public void setGlobalBehavior(Consumer<GlobalBehaviorBuilder> options) {
        GlobalBehaviorBuilder builder = GlobalBehaviorBuilder.builder();
        options.accept(builder);
        this.globalBehavior = builder.build();
    }

    public GlobalBehavior getGlobalBehavior() {
        return globalBehavior;
    }

    public FabricScheduler getScheduler() {
        return scheduler;
    }

    public MinecraftServerAudiences getAudiences() {
        return audiences;
    }
}
