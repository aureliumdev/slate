package dev.aurelium.slate.fabric.function;

import dev.aurelium.slate.fabric.info.MenuInfo;

import java.util.Map;

@FunctionalInterface
public interface PropertyProvider {

    /**
     * Gets the default properties of a menu.
     *
     * @param info the {@link MenuInfo} context object
     * @return the default properties
     */
    Map<String, Object> get(MenuInfo info);

}
