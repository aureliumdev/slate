package dev.aurelium.slate.menu;

import dev.aurelium.slate.component.MenuComponent;
import dev.aurelium.slate.context.ContextGroup;
import dev.aurelium.slate.position.PositionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("unused")
public interface ActiveMenu {

    String getName();

    /**
     * Hides or shows an item in the current menu, will not delete the item in configs.
     * Will not take effect unless the menu is reloaded.
     *
     * @param itemName The name of the item to hide
     */
    void setHidden(String itemName, boolean hidden);

    /**
     * Gets the current page of the menu, 0 is the first page
     *
     * @return The current page number, 0 if there is no pagination set up
     */
    int getCurrentPage();

    /**
     * Gets the total number of pages in the menu
     *
     * @return The total number of pages, 1 if there is no pagination set up
     */
    int getTotalPages();

    /**
     * Gets a property from a name cast to a type.
     *
     * @param name the property name
     * @return the property value, or null if it isn't defined
     * @param <T> the type of the value
     */
    <T> T property(String name);

    /**
     * Gets a property from a name cast to a type or a default value.
     *
     * @param name the property name
     * @param def the default fallback value
     * @return the property value, or def
     * @param <T> the type of the value
     */
    <T> T property(String name, T def);

    /**
     * Gets a property from the menu's properties with the given name.
     *
     * @param name the name of the property
     * @return the property value as an Object
     */
    Object getProperty(String name);

    /**
     * Gets a property from the menu's properties with the given name, or returns the default value if the property does not exist.
     *
     * @param name the name of the property
     * @param def the default value to return if the property does not exist
     * @return the property value as an Object, or the default value if the property does not exist
     */
    Object getProperty(String name, Object def);

    /**
     * Gets all properties from the menu's properties.
     *
     * @return a Map of all properties
     */
    Map<String, Object> getProperties();

    /**
     * Sets a property in the menu's properties with the given name and value.
     *
     * @param name the name of the property
     * @param value the value of the property
     */
    void setProperty(String name, Object value);

    /**
     * Sets a property only if a property with the given name has not been set.
     *
     * @param name the name of the property
     * @param value the value of the property
     */
    void defaultProperty(String name, Object value);

    /**
     * Reloads the menu for the player as if it was reopened.
     */
    void reload();

    /**
     * Sets the cooldown of an item in the current menu. Items on cooldown will not be able to be clicked.
     *
     * @param itemName the name of the item to set the cooldown for
     * @param cooldown the cooldown in ticks
     */
    void setCooldown(String itemName, int cooldown);

    /**
     * Gets the value of an option from the menu's configurable options.
     *
     * @param key the key of the option
     * @return the value of the option as an Object
     */
    @Nullable Object getOption(String key);

    /**
     * Gets the value of an option from the menu's configurable options cast to a type.
     *
     * @param clazz the class of the option to cast to
     * @param key the key of the option
     * @return the value of the option as the specified type, or null if the option does not exist
     * @param <T> the type of the option
     */
    @Nullable <T> T getOption(Class<T> clazz, String key);

    /**
     * Gets the value of an option from the menu's configurable options, or returns the default value if the option does not exist.
     *
     * @param key the key of the option
     * @param def the default value to return if the option does not exist
     * @return the value of the option as an Object, or the default value if the option does not exist
     */
    Object getOption(String key, Object def);

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
    <T> T getOption(Class<T> clazz, String key, T def);

    @Nullable Object getItemOption(String itemName, String key);

    Object getItemOption(String itemName, String key, Object def);

    Map<String, MenuComponent> getComponents();

    Map<String, String> getFormats();

    /**
     * Gets a format from the menu's formats, returns the key if the format does not exist
     *
     * @param key The key of the format
     * @return The format, or the key if the format does not exist
     */
    @NotNull String getFormat(String key);

    <T> void setPositionProvider(String templateName, T context, PositionProvider provider);

    Map<String, ContextGroup> getContextGroups(String templateName);

}
