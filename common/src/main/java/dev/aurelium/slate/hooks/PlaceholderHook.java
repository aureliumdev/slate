package dev.aurelium.slate.hooks;

import dev.aurelium.slate.ref.PlayerRef;

public interface PlaceholderHook {

    String setPlaceholders(PlayerRef player, String text);

}
