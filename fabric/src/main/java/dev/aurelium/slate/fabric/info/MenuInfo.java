package dev.aurelium.slate.fabric.info;

import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.menu.ActiveMenu;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

/**
 * Represents contextual information about an instance of a menu.
 */
public class MenuInfo {

    private final Slate slate;
    private final ServerPlayer player;
    private final ActiveMenu menu;

    public MenuInfo(Slate slate, ServerPlayer player, ActiveMenu menu) {
        this.slate = slate;
        this.player = player;
        this.menu = menu;
    }

    /**
     * Gets the player viewing the menu.
     *
     * @return the player
     */
    public ServerPlayer player() {
        return player;
    }

    /**
     * Gets the {@link ActiveMenu} instance of the menu. This can be used to get properties of the menu and
     * modify the menu while its open.
     *
     * @return the menu
     */
    public ActiveMenu menu() {
        return menu;
    }

    /**
     * Gets the locale of the player viewing the menu as defined by the {@link dev.aurelium.slate.fabric.builder.GlobalBehavior#localeProvider()}.
     * If the locale provider is not set, this will always return {@code Locale.ENGLISH}. This is useful if you
     * have player-dependent locales.
     *
     * @return the locale of the player
     */
    public Locale locale() {
        return slate.getGlobalBehavior().localeProvider().get(player);
    }
}
