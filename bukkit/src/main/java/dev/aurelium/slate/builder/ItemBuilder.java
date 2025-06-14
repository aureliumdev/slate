package dev.aurelium.slate.builder;

import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.function.ItemClicker;
import dev.aurelium.slate.function.ItemModifier;
import dev.aurelium.slate.function.ItemReplacer;
import dev.aurelium.slate.function.MenuListener;
import dev.aurelium.slate.info.ItemInfo;
import dev.aurelium.slate.info.PlaceholderInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to define functionality for an item in a menu.
 */
public class ItemBuilder {

    private final Map<String, ItemReplacer> replacers = new HashMap<>();
    private ItemReplacer anyReplacer = p -> null; // Default anyReplacer doesn't replace by returning null
    private final Map<ClickTrigger, ItemClicker> clickers = new HashMap<>();
    private ItemModifier modifier = ItemInfo::item;
    private MenuListener initListener = m -> {};

    /**
     * Create a new ItemBuilder instance
     *
     * @return the item builder
     */
    public static ItemBuilder builder() {
        return new ItemBuilder();
    }

    /**
     * Replaces a specific placeholder in the item's display name or lore.
     *
     * @param from the name of the placeholder without the curly braces
     * @param replacer the {@link ItemReplacer} function
     * @return the item builder
     */
    public ItemBuilder replace(String from, ItemReplacer replacer) {
        replacers.put(from, replacer);
        return this;
    }

    /**
     * Replaces any placeholder in the item's display name or lore. The replacer function is
     * run for every placeholder that is found in the item's display name or lore.
     * The name of the placeholder being checked can be accessed using {@link PlaceholderInfo#placeholder()}.
     *
     * @param replacer the {@link ItemReplacer} function
     * @return the item builder
     */
    public ItemBuilder replaceAny(ItemReplacer replacer) {
        anyReplacer = replacer;
        return this;
    }

    /**
     * Defines a consumer to run when the item is clicked by the player using any button.
     *
     * @param clicker the {@link ItemClicker} consumer, which takes an {@link ItemInfo} parameter
     * @return the item builder
     */
    public ItemBuilder onClick(ItemClicker clicker) {
        clickers.put(ClickTrigger.ANY, clicker);
        return this;
    }

    /**
     * Defines a consumer to run when the item is clicked by the player using a specific button.
     *
     * @param action the specific {@link ClickTrigger} button to run the consumer for
     * @param clicker the {@link ItemClicker} consumer, which takes an {@link ItemInfo} parameter
     * @return the item builder
     */
    public ItemBuilder onClick(ClickTrigger action, ItemClicker clicker) {
        clickers.put(action, clicker);
        return this;
    }

    /**
     * Modifies the item before it is displayed to the player.
     *
     * @param modifier the {@link ItemModifier} function
     * @return the item builder
     */
    public ItemBuilder modify(ItemModifier modifier) {
        this.modifier = modifier;
        return this;
    }

    /**
     * Defines a consumer to run when the item is initialized in the menu. This does not
     * affect the item's display, use {@link #modify(ItemModifier)} for that.
     *
     * @param listener the {@link MenuListener} consumer, which takes an {@link ItemInfo} parameter
     * @return the item builder
     */
    public ItemBuilder initialize(MenuListener listener) {
        this.initListener = listener;
        return this;
    }

    public BuiltItem build() {
        return new BuiltItem(replacers, anyReplacer, clickers, modifier, initListener);
    }

}
