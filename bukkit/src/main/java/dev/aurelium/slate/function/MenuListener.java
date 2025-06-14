package dev.aurelium.slate.function;

import dev.aurelium.slate.info.MenuInfo;

@FunctionalInterface
public interface MenuListener {

    /**
     * Code to run when a menu is opened.
     *
     * @param info the {@link MenuInfo} context object
     */
    void handle(MenuInfo info);

}
