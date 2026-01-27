package dev.aurelium.slate.menu;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.trigger.MenuTrigger;
import dev.aurelium.slate.builder.BuiltTemplate;
import dev.aurelium.slate.component.ComponentParser;
import dev.aurelium.slate.component.MenuComponent;
import dev.aurelium.slate.context.ContextProvider;
import dev.aurelium.slate.fill.*;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.parser.ConfigurateItemParser;
import dev.aurelium.slate.item.parser.SingleItemParser;
import dev.aurelium.slate.item.parser.TemplateItemParser;
import dev.aurelium.slate.util.YamlLoader;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BukkitMenuLoader extends MenuLoader {

    private final Slate slate;
    private final YamlLoader loader;

    public BukkitMenuLoader(Slate slate, File mainDir, List<File> mergeDirs) {
        super(slate, mainDir, mergeDirs);
        this.slate = slate;
        this.loader = new YamlLoader(slate.getPlugin().getLogger(), slate.getPlugin().getDataFolder());
    }

    /**
     * Attempts to load a menu from a file
     *
     * @param file The file to load from, must be in Yaml syntax
     * @param menuName The name of the menu to be used when opening
     */
    @Override
    public LoadedMenu loadMenu(File file, String menuName) throws ConfigurateException, RuntimeException {
        ConfigurationNode config = mergeAndLoad(file);

        var itemParser = new ConfigurateItemParser(slate);

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
                    MenuItem item = new SingleItemParser(slate, itemParser).parse(itemSection, menuName);
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
                        MenuItem item = new TemplateItemParser<>(slate, itemParser, contextProvider).parse(templateSection, menuName);
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
            FillItem fillItem = new FillItemParser(slate, itemParser).parse(fillSection, menuName);
            fillData = new FillData(fillItem, new SlotParser().parse(fillSection, size), fillEnabled);
        } else {
            fillData = new FillData(BukkitFillData.getDefault(slate), null, false);
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
        Map<String, Object> options = BukkitMenuLoader.loadOptions(config);

        Map<MenuTrigger, List<Action>> actions = loadActions(config, menuName);
        // Add menu to map
        return new LoadedMenu(menuName, title, size, items, components, formats, fillData, options, actions);
    }

    @Override
    public Map<MenuTrigger, List<Action>> loadActions(ConfigurationNode config, String menuName) {
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

    @Override
    public void generateDefaultOptions(String menuName, File file, ConfigurationNode mainConfig) throws SerializationException {
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

}
