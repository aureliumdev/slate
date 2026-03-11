package dev.aurelium.slate.fabric.function;


import dev.aurelium.slate.fabric.info.MenuInfo;

import java.util.Set;

@FunctionalInterface
public interface DefinedContexts<T> {

    /**
     * Gets the set of defined contexts for a template. This is the set of context instances used to
     * determine how many possible instances of a template can be created. This must be defined for a template
     * when built using {@link dev.aurelium.slate.fabric.builder.TemplateBuilder#definedContexts(DefinedContexts)}.
     *
     * @param info the {@link MenuInfo} context object
     * @return the set of defined contexts
     */
    Set<T> get(MenuInfo info);

}
