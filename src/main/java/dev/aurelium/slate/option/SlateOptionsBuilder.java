package dev.aurelium.slate.option;

import dev.aurelium.slate.function.ItemMetaParser;
import dev.aurelium.slate.item.provider.KeyedItemProvider;
import dev.aurelium.slate.util.Validate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder for creating {@link SlateOptions} to pass to the {@link dev.aurelium.slate.Slate} constructor.
 */
public class SlateOptionsBuilder {

    private File mainDirectory;
    private List<File> mergeDirectories = new ArrayList<>();
    private int loreWrappingWidth = 40;
    private KeyedItemProvider keyedItemProvider = k -> null;
    private final Map<String, ItemMetaParser> itemMetaParsers = new HashMap<>();
    private boolean removalProtection = false;
    private boolean isMock = false;

    /**
     * Sets the main directory where menu files are located. This is a required option.
     * Only menu files that are present on the server will be loaded.
     *
     * @param mainDirectory the main directory
     * @return the builder
     */
    public SlateOptionsBuilder mainDirectory(File mainDirectory) {
        this.mainDirectory = mainDirectory;
        return this;
    }

    /**
     * Sets the directories to merge with the main directory. Menu files in merge directories that match
     * a file in the main directory will be merged together. New files in merge directories will be loaded
     * as new menus.
     *
     * @param mergeDirectories the merge directories
     * @return the builder
     */
    public SlateOptionsBuilder mergeDirectories(List<File> mergeDirectories) {
        this.mergeDirectories = mergeDirectories;
        return this;
    }

    /**
     * Sets the number of characters in a line before lore wraps to the next line.
     * This only applies to text lore that has wrap: true in the menu file.
     * Default is 40.
     *
     * @param loreWrappingWidth the lore wrapping width
     * @return the builder
     */
    public SlateOptionsBuilder loreWrappingWidth(int loreWrappingWidth) {
        this.loreWrappingWidth = loreWrappingWidth;
        return this;
    }

    /**
     * Sets the provider for keyed items. This is used to provide complex ItemStack instances for
     * items in the menu file that define a key option instead of a material.
     *
     * @param keyedItemProvider the keyed item provider
     * @return the builder
     */
    public SlateOptionsBuilder keyedItemProvider(KeyedItemProvider keyedItemProvider) {
        this.keyedItemProvider = keyedItemProvider;
        return this;
    }

    /**
     * Adds a custom item meta parser. This is used to parse custom item meta options for base items in the menu file.
     *
     * @param name the config key of the node to parse
     * @param parser the {@link ItemMetaParser} parser function
     * @return the builder
     */
    public SlateOptionsBuilder itemMetaParser(String name, ItemMetaParser parser) {
        this.itemMetaParsers.put(name, parser);
        return this;
    }

    /**
     * Adds multiple custom item meta parsers. This is used to parse custom item meta options for base items in the menu file.
     *
     * @param parsers the map of config keys to {@link ItemMetaParser} parser functions
     * @return the builder
     */
    public SlateOptionsBuilder itemMetaParsers(Map<String, ItemMetaParser> parsers) {
        this.itemMetaParsers.putAll(parsers);
        return this;
    }

    public SlateOptionsBuilder removalProtection(boolean removalProtection) {
        this.removalProtection = removalProtection;
        return this;
    }

    public SlateOptionsBuilder isMock(boolean isMock) {
        this.isMock = isMock;
        return this;
    }

    /**
     * Builds the {@link SlateOptions} object.
     *
     * @return the slate options
     */
    public SlateOptions build() {
        Validate.notNull(mainDirectory, "mainDirectory not defined in SlateOptions");
        return new SlateOptions(mainDirectory, mergeDirectories, loreWrappingWidth, keyedItemProvider, itemMetaParsers, removalProtection, isMock);
    }

}
