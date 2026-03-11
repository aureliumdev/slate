package dev.aurelium.slate.fabric.hooks;

import dev.aurelium.slate.hooks.PlaceholderHook;
import dev.aurelium.slate.ref.PlayerRef;

public class FabricPlaceholderHook implements PlaceholderHook {

    // Empty for now
    @Override
    public String setPlaceholders(PlayerRef player, String text) {
        return text;
    }
}
