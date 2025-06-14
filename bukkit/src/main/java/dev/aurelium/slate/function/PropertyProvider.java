package dev.aurelium.slate.function;

import dev.aurelium.slate.info.MenuInfo;

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
