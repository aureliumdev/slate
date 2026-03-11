package dev.aurelium.slate.fabric.option;


import dev.aurelium.slate.fabric.function.ItemMetaParser;
import dev.aurelium.slate.fabric.item.provider.KeyedItemProvider;

import java.io.File;
import java.util.List;
import java.util.Map;

public record SlateOptions(
        File mainDirectory,
        List<File> mergeDirectories,
        int loreWrappingWidth,
        KeyedItemProvider keyedItemProvider,
        Map<String, ItemMetaParser> itemMetaParsers,
        boolean removalProtection,
        boolean isMock
) {

    public static SlateOptionsBuilder builder() {
        return new SlateOptionsBuilder();
    }

}
