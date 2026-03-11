package dev.aurelium.slate.fabric.function;

import dev.aurelium.slate.fabric.info.ComponentInfo;

@FunctionalInterface
public interface ComponentVisibility<T> {

    /**
     * Determines if a component should be shown in a template or item.
     *
     * @param info the {@link ComponentInfo} context object
     * @return true if the component should be shown, false otherwise
     */
    boolean shouldShow(ComponentInfo<T> info);

}
