package dev.aurelium.slate.menu;

import dev.aurelium.slate.SlateLibrary;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.trigger.MenuTrigger;
import dev.aurelium.slate.util.YamlLoader;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.*;

public abstract class MenuLoader {

    private final SlateLibrary slate;
    private final File mainDir;
    private final List<File> mergeDirs;
    protected final YamlLoader loader;

    public MenuLoader(SlateLibrary slate, File mainDir, List<File> mergeDirs) {
        this.slate = slate;
        this.mainDir = mainDir;
        this.mergeDirs = mergeDirs;
        this.loader = new YamlLoader(slate.getLogger());
    }

    public int loadMenus() {
        File[] files = mainDir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return 0;

        int menusLoaded = 0;
        Set<String> mainLoaded = new HashSet<>();

        for (File menuFile : files) {
            try {
                String menuName = loadAndAddMenu(menuFile);
                menusLoaded++;
                mainLoaded.add(menuName);
            } catch (ConfigurateException | RuntimeException e) {
                slate.getLogger().warning("Error loading menu file " + menuFile.getName());
                e.printStackTrace();
            }
        }

        menusLoaded += loadExternalMenus(mainLoaded);

        return menusLoaded;
    }

    private int loadExternalMenus(Set<String> mainLoaded) {
        int menusLoaded = 0;
        // Load new menus from mergeDirs
        for (File mergeDir : mergeDirs) { // Each merge directory
            if (!mergeDir.isDirectory()) continue;

            File[] files = mergeDir.listFiles((d, name) -> name.endsWith(".yml"));
            if (files == null) continue;

            for (File menuFile : files) { // Each menu file in external directory
                String menuName = menuFile.getName().substring(0, menuFile.getName().lastIndexOf("."));

                // Skip if already loaded and merged with a main dir menu file
                if (mainLoaded.contains(menuName)) {
                    continue;
                }

                try {
                    loadAndAddMenu(menuFile);
                    menusLoaded++;
                } catch (ConfigurateException e) {
                    slate.getLogger().warning("Error loading menu file " + menuFile.getName());
                    e.printStackTrace();
                }
            }
        }
        return menusLoaded;
    }

    private String loadAndAddMenu(File file) throws ConfigurateException, RuntimeException {
        String menuName = file.getName();
        int pos = menuName.lastIndexOf(".");
        if (pos > 0) {
            menuName = menuName.substring(0, pos);
        }
        LoadedMenu menu = loadMenu(file, menuName);
        slate.addLoadedMenu(menu);
        return menuName;
    }

    protected ConfigurationNode mergeAndLoad(File mainFile) throws ConfigurateException {
        ConfigurationNode base = loader.loadUserFile(mainFile);
        List<ConfigurationNode> nodesToMerge = new ArrayList<>();
        nodesToMerge.add(base);

        for (File mergeDir : mergeDirs) {
            if (!mergeDir.isDirectory()) continue;

            File[] files = mergeDir.listFiles((d, name) -> name.equals(mainFile.getName()));
            if (files == null || files.length == 0) continue;

            File mergingFile = files[0];
            ConfigurationNode mergingNode = loader.loadUserFile(mergingFile);
            nodesToMerge.add(mergingNode);
        }

        return loader.mergeNodes(nodesToMerge.toArray(new ConfigurationNode[0]));
    }

    /**
     * Attempts to load a menu from a file
     *
     * @param file The file to load from, must be in Yaml syntax
     * @param menuName The name of the menu to be used when opening
     */
    public abstract LoadedMenu loadMenu(File file, String menuName) throws ConfigurateException;

    public abstract Map<MenuTrigger, List<Action>> loadActions(ConfigurationNode config, String menuName);

    public abstract void generateDefaultOptions(String menuName, File file, ConfigurationNode mainConfig) throws SerializationException;

    public static Map<String, Object> loadOptions(ConfigurationNode config) {
        Map<String, Object> options = new HashMap<>();
        ConfigurationNode optionSection = config.node("options");
        for (Object keyObj : optionSection.childrenMap().keySet()) {
            String key = (String) keyObj;
            if (optionSection.node(keyObj).isMap()) continue;
            Object value = optionSection.node(key).raw();
            options.put(key, value);
        }
        return options;
    }

}
