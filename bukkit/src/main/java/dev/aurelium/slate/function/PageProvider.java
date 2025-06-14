package dev.aurelium.slate.function;

import dev.aurelium.slate.info.MenuInfo;

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
