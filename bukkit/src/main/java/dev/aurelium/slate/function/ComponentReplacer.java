package dev.aurelium.slate.function;

import dev.aurelium.slate.info.ComponentPlaceholderInfo;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ComponentReplacer<T> {

    /**
     * Replaces a placeholder in a component.
     *
     * @param info the {@link ComponentPlaceholderInfo} context object
     * @return the replacement text, or null if no replacement should be made
     */
    @Nullable
    String replace(ComponentPlaceholderInfo<T> info);

}
