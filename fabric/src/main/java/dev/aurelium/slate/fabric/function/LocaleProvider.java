package dev.aurelium.slate.fabric.function;

import dev.aurelium.slate.fabric.info.MenuInfo;
import net.minecraft.server.level.ServerPlayer;

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
    Locale get(ServerPlayer player);

}
