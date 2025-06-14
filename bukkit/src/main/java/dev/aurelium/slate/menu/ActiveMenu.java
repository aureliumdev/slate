package dev.aurelium.slate.menu;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.component.MenuComponent;
import dev.aurelium.slate.context.ContextGroup;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.TemplateItem;
import dev.aurelium.slate.item.active.ActiveItem;
import dev.aurelium.slate.position.PositionProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ActiveMenu {

    private final MenuInventory menuInventory;

    public ActiveMenu(MenuInventory menuInventory) {
        this.menuInventory = menuInventory;
    }

    public static ActiveMenu empty(Slate slate, Player player) {
        return new EmptyActiveMenu(slate, player);
    }

    public String getName() {
        return menuInventory.getMenu().name();
    }

    /**
     * Hides or shows an item in the current menu, will not delete the item in configs.
     * Will not take effect unless the menu is reloaded.
     *
     * @param itemName The name of the item to hide
     */
    public void setHidden(String itemName, boolean hidden) {
        ActiveItem activeItem = menuInventory.getActiveItem(itemName);
        if (activeItem != null) {
            activeItem.setHidden(hidden);
        }
    }

    /**
     * Gets the current page of the menu, 0 is the first page
     *
     * @return The current page number, 0 if there is no pagination set up
     */
    public int getCurrentPage() {
        return menuInventory.getCurrentPage();
    }

    /**
     * Gets the total number of pages in the menu
     *
     * @return The total number of pages, 1 if there is no pagination set up
     */
    public int getTotalPages() {
        return menuInventory.getTotalPages();
    }

    /**
     * Gets a property from a name cast to a type.
     *
     * @param name the property name
     * @return the property value, or null if it isn't defined
     * @param <T> the type of the value
     */
    @SuppressWarnings("unchecked")
    public <T> T property(String name) {
        try {
            return (T) menuInventory.getProperties().get(name);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets a property from a name cast to a type or a default value.
     *
     * @param name the property name
     * @param def the default fallback value
     * @return the property value, or def
     * @param <T> the type of the value
     */
    @SuppressWarnings("unchecked")
    public <T> T property(String name, T def) {
        try {
            return (T) menuInventory.getProperties().getOrDefault(name, def);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return def;
        }
    }

    /**
     * Gets a property from the menu's properties with the given name.
     *
     * @param name the name of the property
     * @return the property value as an Object
     */
    public Object getProperty(String name) {
        return menuInventory.getProperties().get(name);
    }

    /**
     * Gets a property from the menu's properties with the given name, or returns the default value if the property does not exist.
     *
     * @param name the name of the property
     * @param def the default value to return if the property does not exist
     * @return the property value as an Object, or the default value if the property does not exist
     */
    public Object getProperty(String name, Object def) {
        Object value = menuInventory.getProperties().get(name);
        if (value != null) {
            return value;
        } else {
            return def;
        }
    }

    /**
     * Gets all properties from the menu's properties.
     *
     * @return a Map of all properties
     */
    public Map<String, Object> getProperties() {
        return menuInventory.getProperties();
    }

    /**
     * Sets a property in the menu's properties with the given name and value.
     *
     * @param name the name of the property
     * @param value the value of the property
     */
    public void setProperty(String name, Object value) {
        menuInventory.getProperties().put(name, value);
    }

    /**
     * Sets a property only if a property with the given name has not been set.
     *
     * @param name the name of the property
     * @param value the value of the property
     */
    public void defaultProperty(String name, Object value) {
        if (!menuInventory.getProperties().containsKey(name)) {
            menuInventory.getProperties().put(name, value);
        }
    }

    /**
     * Reloads the menu for the player as if it was reopened.
     */
    public void reload() {
        menuInventory.init(menuInventory.getPlayer(), menuInventory.getContents());
    }

    /**
     * Sets the cooldown of an item in the current menu. Items on cooldown will not be able to be clicked.
     *
     * @param itemName the name of the item to set the cooldown for
     * @param cooldown the cooldown in ticks
     */
    public void setCooldown(String itemName, int cooldown) {
        ActiveItem activeItem = menuInventory.getActiveItem(itemName);
        if (activeItem != null) {
            activeItem.setCooldown(cooldown);
            menuInventory.setToUpdate(activeItem);
        }
    }

    /**
     * Gets the value of an option from the menu's configurable options.
     *
     * @param key the key of the option
     * @return the value of the option as an Object
     */
    @Nullable
    public Object getOption(String key) {
        return menuInventory.getMenu().options().get(key);
    }

    /**
     * Gets the value of an option from the menu's configurable options cast to a type.
     *
     * @param clazz the class of the option to cast to
     * @param key the key of the option
     * @return the value of the option as the specified type, or null if the option does not exist
     * @param <T> the type of the option
     */
    @Nullable
    public <T> T getOption(Class<T> clazz, String key) {
        try {
            return clazz.cast(menuInventory.getMenu().options().get(key));
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Gets the value of an option from the menu's configurable options, or returns the default value if the option does not exist.
     *
     * @param key the key of the option
     * @param def the default value to return if the option does not exist
     * @return the value of the option as an Object, or the default value if the option does not exist
     */
    public Object getOption(String key, Object def) {
        Object obj = menuInventory.getMenu().options().get(key);
        if (obj != null) {
            return obj;
        } else {
            return def;
        }
    }

    /**
     * Gets the value of an option from the menu's configurable options cast to a type, or returns the default value if the
     * option does not exist.
     *
     * @param clazz the class of the option to cast to
     * @param key the key of the option
     * @param def the default value to return if the option does not exist
     * @return the value of the option as the specified type, or the default value if the option does not exist
     * @param <T> the type of the option
     */
    public <T> T getOption(Class<T> clazz, String key, T def) {
        try {
            T result = clazz.cast(menuInventory.getMenu().options().get(key));
            if (result != null) {
                return result;
            } else {
                return def;
            }
        } catch (ClassCastException e) {
            return def;
        }
    }

    @Nullable
    public Object getItemOption(String itemName, String key) {
        MenuItem menuItem = menuInventory.getMenu().items().get(itemName);
        if (menuItem != null) {
            return menuItem.getOptions().get(key);
        }
        return null;
    }

    public Object getItemOption(String itemName, String key, Object def) {
        MenuItem menuItem = menuInventory.getMenu().items().get(itemName);
        if (menuItem != null) {
            Object obj = menuItem.getOptions().get(key);
            if (obj != null) {
                return obj;
            }
        }
        return def;
    }

    public Map<String, MenuComponent> getComponents() {
        return menuInventory.getMenu().components();
    }

    public Map<String, String> getFormats() {
        return menuInventory.getMenu().formats();
    }

    /**
     * Gets a format from the menu's formats, returns the key if the format does not exist
     *
     * @param key The key of the format
     * @return The format, or the key if the format does not exist
     */
    @NotNull
    public String getFormat(String key) {
        return menuInventory.getMenu().formats().getOrDefault(key, key);
    }

    @SuppressWarnings("unchecked")
    public <T> void setPositionProvider(String templateName, T context, PositionProvider provider) {
        MenuItem menuItem = menuInventory.getMenu().items().get(templateName);
        if (menuItem instanceof TemplateItem) {
            TemplateItem<T> templateItem = (TemplateItem<T>) menuItem;
            templateItem.getPositionsMap().put(context, provider);
        }
    }

    public Map<String, ContextGroup> getContextGroups(String templateName) {
        MenuItem menuItem = menuInventory.getMenu().items().get(templateName);
        if (menuItem instanceof TemplateItem<?> templateItem) {
            return templateItem.getContextGroups();
        }
        return new HashMap<>();
    }

}
