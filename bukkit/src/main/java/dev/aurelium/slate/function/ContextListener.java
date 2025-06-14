package dev.aurelium.slate.function;

import dev.aurelium.slate.info.TemplateInfo;

@FunctionalInterface
public interface ContextListener<T> {

    /**
     * Code to run when a template is initialized
     *
     * @param info the {@link TemplateInfo} context object
     */
    void handle(TemplateInfo<T> info);

}
