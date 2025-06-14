package dev.aurelium.slate.menu;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.trigger.MenuTrigger;
import dev.aurelium.slate.builder.BuiltTemplate;
import dev.aurelium.slate.component.ComponentParser;
import dev.aurelium.slate.component.MenuComponent;
import dev.aurelium.slate.context.ContextProvider;
import dev.aurelium.slate.fill.FillData;
import dev.aurelium.slate.fill.FillItem;
import dev.aurelium.slate.fill.FillItemParser;
import dev.aurelium.slate.fill.SlotParser;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.parser.SingleItemParser;
import dev.aurelium.slate.item.parser.TemplateItemParser;
import dev.aurelium.slate.util.YamlLoader;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MenuLoader {

    private final Slate slate;
    private final File mainDir;
    private final List<File> mergeDirs;
    private final YamlLoader loader;

    public MenuLoader(Slate slate, File mainDir, List<File> mergeDirs) {
        this.slate = slate;
        this.mainDir = mainDir;
        this.mergeDirs = mergeDirs;
        this.loader = new YamlLoader(slate.getPlugin());
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
                slate.getPlugin().getLogger().warning("Error loading menu file " + menuFile.getName());
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
                    slate.getPlugin().getLogger().warning("Error loading menu file " + menuFile.getName());
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

    private ConfigurationNode mergeAndLoad(File mainFile) throws ConfigurateException {
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
    private LoadedMenu loadMenu(File file, String menuName) throws ConfigurateException, RuntimeException {
        ConfigurationNode config = mergeAndLoad(file);

        String title = config.node("title").getString(menuName);
        int size = config.node("size").getInt(6);

        Map<String, MenuItem> items = new LinkedHashMap<>();
        // Load single items
        ConfigurationNode itemsSection = config.node("items");
        if (!itemsSection.virtual()) {
            for (Object keyObj: itemsSection.childrenMap().keySet()) {
                String itemName = (String) Objects.requireNonNull(keyObj);
                ConfigurationNode itemSection = itemsSection.node(keyObj);
                if (!itemSection.virtual()) {
                    MenuItem item = new SingleItemParser(slate).parse(itemSection, menuName);
                    items.put(itemName, item);
                }
            }
        }
        // Load template items
        ConfigurationNode templatesSection = config.node("templates");
        if (!templatesSection.virtual()) {
            for (Object keyObj : templatesSection.childrenMap().keySet()) {
                String templateName = (String) Objects.requireNonNull(keyObj);
                ConfigurationNode templateSection = templatesSection.node(keyObj);
                if (!templateSection.virtual()) {
                    ContextProvider<?> contextProvider = null;
                    BuiltTemplate<?> builtTemplate = slate.getBuiltMenu(menuName).templates().get(templateName);
                    if (builtTemplate != null) {
                        contextProvider = slate.getContextManager().getContextProvider(builtTemplate.contextType());
                    }
                    if (contextProvider != null) {
                        MenuItem item = new TemplateItemParser<>(slate, contextProvider).parse(templateSection, menuName);
                        items.put(templateName, item);
                    }
                }
            }
        }
        // Load fill item
        ConfigurationNode fillSection = config.node("fill");
        FillData fillData;
        if (!fillSection.virtual()) {
            boolean fillEnabled = fillSection.node("enabled").getBoolean(false);
            FillItem fillItem = new FillItemParser(slate).parse(fillSection, menuName);
            fillData = new FillData(fillItem, new SlotParser().parse(fillSection, size), fillEnabled);
        } else {
            fillData = new FillData(FillItem.getDefault(slate), null, false);
        }
        // Load components
        Map<String, MenuComponent> components = new HashMap<>();

        ConfigurationNode componentsSection = config.node("components");
        if (!componentsSection.virtual()) {
            for (Object keyObj : componentsSection.childrenMap().keySet()) {
                String name = (String) Objects.requireNonNull(keyObj);
                ConfigurationNode componentNode = componentsSection.node(keyObj);
                if (!componentNode.virtual()) {
                    components.put(name, new ComponentParser(slate).parse(componentNode));
                }
            }
        }
        // Load formats
        Map<String, String> formats = new HashMap<>();
        for (Object keyObj : config.node("formats").childrenMap().keySet()) {
            String key = (String) keyObj;
            String value = config.node("formats").node(keyObj).getString();
            if (value != null) {
                formats.put(key, value);
            }
        }

        generateDefaultOptions(menuName, file, config);
        Map<String, Object> options = MenuLoader.loadOptions(config);

        Map<MenuTrigger, List<Action>> actions = loadActions(config, menuName);
        // Add menu to map
        return new LoadedMenu(menuName, title, size, items, components, formats, fillData, options, actions);
    }

    private Map<MenuTrigger, List<Action>> loadActions(ConfigurationNode config, String menuName) {
        Map<MenuTrigger, List<Action>> actions = new LinkedHashMap<>();
        for (MenuTrigger menuTrigger : MenuTrigger.values()) {
            String id = menuTrigger.getId();
            if (!config.node(id).virtual()) {
                List<Action> clickActions = slate.getActionManager().parseActions(config.node(id), menuName);
                actions.put(menuTrigger, clickActions);
            }
        }
        return actions;
    }

    private void generateDefaultOptions(String menuName, File file, ConfigurationNode mainConfig) throws SerializationException {
        Map<String, Object> defaultOptions = slate.getBuiltMenu(menuName).defaultOptions();
        if (defaultOptions == null) {
            return;
        }
        // Create options section if it does not exist
        ConfigurationNode config = mainConfig.node("options");
        // Loop through each option and set default if option does not exist
        boolean changed = false;
        for (Map.Entry<String, Object> entry : defaultOptions.entrySet()) {
            if (config.node(entry.getKey()).virtual()) {
                config.node(entry.getKey()).set(entry.getValue());
                if (!changed) {
                    changed = true;
                }
            }
        }
        if (changed) { // Save file if modified
            try {
                loader.saveFile(file, mainConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
