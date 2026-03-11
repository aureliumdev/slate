package dev.aurelium.slate.fabric.builder;

import dev.aurelium.slate.fabric.function.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * Class used to define functionality for a menu. This is used in conjunction with the menu file
 * and individual {@link ItemBuilder}, {@link TemplateBuilder}, and {@link ComponentBuilder} instances to
 * render a menu shown to a player.
 */
public class MenuBuilder {

    private final Map<String, ItemBuilder> itemBuilders = new HashMap<>();
    private final Map<String, TemplateBuilder<?>> templateBuilders = new HashMap<>();
    private final Map<String, ComponentBuilder<?>> componentBuilders = new HashMap<>();
    private final Map<String, ItemReplacer> pageReplacers = new HashMap<>();
    private final Map<String, ItemReplacer> titleReplacers = new HashMap<>();
    private ItemReplacer titleAnyReplacer = p -> null;
    private PageProvider pageProvider = m -> 1;
    private PropertyProvider propertyProvider = m -> new HashMap<>();
    private ItemModifier fillItem = i -> null;
    private MenuListener openListener = m -> {};
    private MenuListener updateListener = m -> {};
    private Map<String, Object> defaultOptions = new HashMap<>();

    private MenuBuilder() {

    }

    /**
     * Creates a new menu builder instance.
     *
     * @return the menu builder
     */
    public static MenuBuilder builder() {
        return new MenuBuilder();
    }

    /**
     * Add an item to the menu
     *
     * @param name the name of the item
     * @param item a consumer containing a new {@link ItemBuilder}, best used as a lambda
     * @return the menu builder
     */
    public MenuBuilder item(String name, Consumer<ItemBuilder> item) {
        ItemBuilder builder = ItemBuilder.builder();
        item.accept(builder);
        itemBuilders.put(name, builder);
        return this;
    }

    /**
     * Add an item to the menu that accepts a {@link ItemBuilder} instance you must create yourself.
     * It is usually easier to use {@link #item(String, Consumer)}, which provides a builder for you.
     *
     * @param name the name of the item
     * @param builder the item builder
     * @return the menu builder
     */
    public MenuBuilder item(String name, ItemBuilder builder) {
        itemBuilders.put(name, builder);
        return this;
    }

    /**
     * Add a template item to the menu. A template item is an item that can be repeated multiple times with different contexts.
     * Each instance of a template is linked to a context and can have a separate appearance. This must match a template
     * defined in the templates section of the menu file.
     *
     * @param name the name of the template, as defined in the menu file
     * @param contextType the type of the context object that will be passed to the template
     * @param template a consumer containing a new {@link TemplateBuilder}, best used as a lambda
     * @return the menu builder
     * @param <T> the type of the context object
     */
    public <T> MenuBuilder template(String name, Class<T> contextType, Consumer<TemplateBuilder<T>> template) {
        TemplateBuilder<T> builder = TemplateBuilder.builder(contextType);
        template.accept(builder);
        templateBuilders.put(name, builder);
        return this;
    }

    /**
     * Add a template item to the menu that accepts a {@link TemplateBuilder} instance you must create yourself.
     * It is usually easier to use {@link #template(String, Class, Consumer)}, which provides a builder for you.
     *
     * @param name the name of the template, as defined in the menu file
     * @param contextType the type of the context object that will be passed to the template
     * @param builder the template builder
     * @return the menu builder
     * @param <T> the type of the context object
     */
    public <T> MenuBuilder template(String name, Class<T> contextType, TemplateBuilder<T> builder) {
        templateBuilders.put(name, builder);
        return this;
    }

    /**
     * Add a component to the menu. A component is a reusable piece of item lore (list of lore lines) that can
     * be inserted into items and templates. Components can be shown and hidden based on a condition in code.
     * This must match a component defined in the components section of the menu file.
     *
     * @param name the name of the component, as defined in the menu file
     * @param contextType the type of the context object that will be passed to the component, can be null if no context is needed
     * @param template a consumer containing a new {@link ComponentBuilder}, best used as a lambda
     * @return the menu builder
     * @param <T> the type of the context object
     */
    public <T> MenuBuilder component(String name, @Nullable Class<T> contextType, Consumer<ComponentBuilder<T>> template) {
        ComponentBuilder<T> builder = ComponentBuilder.builder(contextType);
        template.accept(builder);
        componentBuilders.put(name, builder);
        return this;
    }

    /**
     * Add a component without a context type to the menu. Components created this way cannot access the
     * context object of templates, so they should be only used in items.
     *
     * @param name the name of the component, as defined in the menu file
     * @param template a consumer containing a new {@link ComponentBuilder}, best used as a lambda
     * @return the menu builder
     */
    public MenuBuilder component(String name, Consumer<ComponentBuilder<?>> template) {
        ComponentBuilder<?> builder = ComponentBuilder.builder(null);
        template.accept(builder);
        componentBuilders.put(name, builder);
        return this;
    }

    /**
     * Add a component to the menu that accepts a {@link ComponentBuilder} instance you must create yourself.
     * It is usually easier to use {@link #component(String, Class, Consumer)}, which provides a builder for you.
     *
     * @param name the name of the component, as defined in the menu file
     * @param contextType the type of the context object that will be passed to the component, can be null if no context is needed
     * @param builder the component builder
     * @return the menu builder
     * @param <T> the type of the context object
     */
    public <T> MenuBuilder component(String name, Class<T> contextType, ComponentBuilder<T> builder) {
        componentBuilders.put(name, builder);
        return this;
    }

    /**
     * Replaces any placeholder on the page that matches the "from" string. This can be in the title,
     * items, templates, and components.
     *
     * @param from the text of the placeholder without curly braces
     * @param replacer the replacer function, best used as a lambda
     * @return the menu builder
     */
    public MenuBuilder replace(String from, ItemReplacer replacer) {
        pageReplacers.put(from, replacer);
        return this;
    }

    /**
     * Replaces a placeholder in the menu title with a value.
     *
     * @param from the text of the placeholder without curly braces
     * @param replacer the replacer function, best used as a lambda
     * @return the menu builder
     */
    public MenuBuilder replaceTitle(String from, ItemReplacer replacer) {
        titleReplacers.put(from, replacer);
        return this;
    }

    /**
     * Replaces any placeholder in the menu title with a value. The {@link ItemReplacer} passed
     * will be called for every placeholder detected in the title. The name of the placeholder being checked
     * can be accessing using {@link dev.aurelium.slate.fabric.info.PlaceholderInfo#placeholder()}.
     *
     * @param replacer the replacer function, best used as a lambda
     * @return the menu builder
     */
    public MenuBuilder replaceTitleAny(ItemReplacer replacer) {
        this.titleAnyReplacer = replacer;
        return this;
    }

    /**
     * Sets the page provider for the menu. The page provider computes the number of pages
     * needed for the menu using a {@link dev.aurelium.slate.fabric.info.MenuInfo} context object.
     *
     * @param provider the page provider
     * @return the menu builder
     */
    public MenuBuilder pages(PageProvider provider) {
        this.pageProvider = provider;
        return this;
    }

    /**
     * Defines default properties for this menu. These properties are used when the menu is opened
     * through click actions defined in other menu files.
     *
     * @param provider the {@link PropertyProvider}, a function that takes a {@link dev.aurelium.slate.fabric.info.MenuInfo}
     *                 context object and returns a {@code Map<String, Object>} of properties
     * @return the menu builder
     */
    public MenuBuilder properties(PropertyProvider provider) {
        this.propertyProvider = provider;
        return this;
    }

    /**
     * Defines a fill item or modifies an existing fill item in the menu. The fill item is used to fill up
     * empty slots in the menu with a repeating item. In most cases, the fill item can be fully defined with the
     * fill section of the menu file. This should only be used if you need to modify the fill item in code.
     *
     * @param modifier the item modifier, a function that takes a {@link dev.aurelium.slate.fabric.info.ItemInfo} context object
     *                 and returns the {@link net.minecraft.world.item.ItemStack} that will be used as the fill item. The
     *                 existing fill item defined from the menu file can be accessed using {@link dev.aurelium.slate.fabric.info.ItemInfo#item()}.
     * @return the menu builder
     */
    public MenuBuilder fillItem(ItemModifier modifier) {
        this.fillItem = modifier;
        return this;
    }

    /**
     * Defines a consumer to run when the menu is opened for a player.
     *
     * @param listener the menu listener, a consumer that takes a {@link dev.aurelium.slate.fabric.info.MenuInfo} context object
     * @return the menu builder
     */
    public MenuBuilder onOpen(MenuListener listener) {
        this.openListener = listener;
        return this;
    }

    /**
     * Defines a consumer to run every tick a menu is open for a player.
     *
     * @param listener the menu listener, a consumer that takes a {@link dev.aurelium.slate.fabric.info.MenuInfo} context object
     * @return the menu builder
     */
    public MenuBuilder onUpdate(MenuListener listener) {
        this.updateListener = listener;
        return this;
    }

    /**
     * Sets default options for the menu, matching values in the options section of a menu file.
     * These options allow Slate to update user menu files automatically with new configurable options you add later.
     *
     * @param defaultOptions a map of default options
     * @return the menu builder
     */
    public MenuBuilder defaultOptions(Map<String, Object> defaultOptions) {
        this.defaultOptions = defaultOptions;
        return this;
    }

    public BuiltMenu build() {
        Map<String, BuiltItem> items = new HashMap<>();
        for (Entry<String, ItemBuilder> entry : itemBuilders.entrySet()) {
            items.put(entry.getKey(), entry.getValue().build());
        }
        Map<String, BuiltTemplate<?>> templates = new HashMap<>();
        for (Entry<String, TemplateBuilder<?>> entry : templateBuilders.entrySet()) {
            templates.put(entry.getKey(), entry.getValue().build());
        }
        Map<String, BuiltComponent<?>> components = new HashMap<>();
        for (Entry<String, ComponentBuilder<?>> entry : componentBuilders.entrySet()) {
            components.put(entry.getKey(), entry.getValue().build());
        }
        return new BuiltMenu(items, templates, components, pageReplacers, titleReplacers, titleAnyReplacer, pageProvider, propertyProvider,
                fillItem, openListener, updateListener, defaultOptions);
    }

}
