package dev.aurelium.slate.fabric.function;

import dev.aurelium.slate.info.MenuInfo;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;

@FunctionalInterface
public interface LocaleProvider {

    /**
     * Gets the locale of a player. This is used in context objects like {@link MenuInfo}
     * to easily get the locale of a player.
     *
     * @param player the player
     * @return the locale
     */
    Locale get(Player player);

}
