package dev.aurelium.slate.fabric.function;

import dev.aurelium.slate.fabric.info.TemplatePlaceholderInfo;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TemplateReplacer<T> {

    /**
     * Replaces a placeholder in a template.
     *
     * @param info the {@link TemplatePlaceholderInfo} context object
     * @return the replaced string, or null if the placeholder should not be replaced
     */
    @Nullable
    String replace(TemplatePlaceholderInfo<T> info);

}
