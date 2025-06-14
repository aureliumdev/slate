package dev.aurelium.slate.info;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.builder.GlobalBehavior;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Represents contextual information about an instance of a menu.
 */
public class MenuInfo {

    private final Slate slate;
    private final Player player;
    private final ActiveMenu menu;

    public MenuInfo(Slate slate, Player player, ActiveMenu menu) {
        this.slate = slate;
        this.player = player;
        this.menu = menu;
    }

    /**
     * Gets the player viewing the menu.
     *
     * @return the player
     */
    public Player player() {
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
     * Gets the locale of the player viewing the menu as defined by the {@link GlobalBehavior#localeProvider()}.
     * If the locale provider is not set, this will always return {@code Locale.ENGLISH}. This is useful if you
     * have player-dependent locales.
     *
     * @return the locale of the player
     */
    public Locale locale() {
        return slate.getGlobalBehavior().localeProvider().get(player);
    }
}
