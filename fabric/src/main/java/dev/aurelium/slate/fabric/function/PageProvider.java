package dev.aurelium.slate.fabric.function;

import dev.aurelium.slate.fabric.info.MenuInfo;

@FunctionalInterface
public interface PageProvider {

    /**
     * Gets the amount of pages in a menu.
     *
     * @param info the {@link MenuInfo} context object
     * @return the amount of pages
     */
    int getPages(MenuInfo info);

}
