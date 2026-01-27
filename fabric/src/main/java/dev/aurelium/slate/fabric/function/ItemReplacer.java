package dev.aurelium.slate.fabric.function;

import dev.aurelium.slate.fabric.info.PlaceholderInfo;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemReplacer {

    /**
     * Replaces a placeholder in an item.
     *
     * @param info the {@link PlaceholderInfo} context object
     * @return the replaced string, or null if the placeholder should not be replaced
     */
    @Nullable
    String replace(PlaceholderInfo info);

}
