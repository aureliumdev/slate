package dev.aurelium.slate.function;

import dev.aurelium.slate.builder.TemplateBuilder;
import dev.aurelium.slate.info.MenuInfo;

import java.util.Set;

@FunctionalInterface
public interface DefinedContexts<T> {

    /**
     * Gets the set of defined contexts for a template. This is the set of context instances used to
     * determine how many possible instances of a template can be created. This must be defined for a template
     * when built using {@link TemplateBuilder#definedContexts(DefinedContexts)}.
     *
     * @param info the {@link MenuInfo} context object
     * @return the set of defined contexts
     */
    Set<T> get(MenuInfo info);

}
