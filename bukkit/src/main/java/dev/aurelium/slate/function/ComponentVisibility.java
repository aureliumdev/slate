package dev.aurelium.slate.function;

import dev.aurelium.slate.info.ComponentInfo;

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
