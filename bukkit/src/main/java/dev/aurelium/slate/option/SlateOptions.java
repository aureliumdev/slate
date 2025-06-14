package dev.aurelium.slate.option;

import dev.aurelium.slate.function.ItemMetaParser;
import dev.aurelium.slate.item.provider.KeyedItemProvider;

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
