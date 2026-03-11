package dev.aurelium.slate.fabric.function;

import dev.aurelium.slate.fabric.item.TemplateClick;

@FunctionalInterface
public interface TemplateClicker<T> {

    /**
     * Code to run when an instance of a template is clicked.
     *
     * @param click the {@link TemplateClick} context object
     */
    void click(TemplateClick<T> click);

}
