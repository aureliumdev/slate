package dev.aurelium.slate.item.parser;

import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.ref.ItemRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public interface ItemParser {

    ItemRef parseBaseItem(ConfigurationNode config);

    @Nullable
    String parseDisplayName(ConfigurationNode section);

    @NotNull
    List<LoreLine> parseLore(ConfigurationNode section);

}
