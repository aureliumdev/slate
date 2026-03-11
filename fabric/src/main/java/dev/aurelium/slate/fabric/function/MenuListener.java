package dev.aurelium.slate.fabric.function;

import dev.aurelium.slate.fabric.info.MenuInfo;

@FunctionalInterface
public interface MenuListener {

    /**
     * Code to run when a menu is opened.
     *
     * @param info the {@link MenuInfo} context object
     */
    void handle(MenuInfo info);

}
