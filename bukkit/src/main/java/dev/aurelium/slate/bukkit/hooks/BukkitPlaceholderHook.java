package dev.aurelium.slate.bukkit.hooks;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.hooks.PlaceholderHook;
import dev.aurelium.slate.ref.PlayerRef;
import me.clip.placeholderapi.PlaceholderAPI;

import static dev.aurelium.slate.bukkit.ref.BukkitPlayerRef.unwrap;

public class BukkitPlaceholderHook implements PlaceholderHook {

    private final Slate slate;

    public BukkitPlaceholderHook(Slate slate) {
        this.slate = slate;
    }

    @Override
    public String setPlaceholders(PlayerRef player, String text) {
        if (!slate.isPlaceholderAPIEnabled()) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(unwrap(player), text);
    }
}
