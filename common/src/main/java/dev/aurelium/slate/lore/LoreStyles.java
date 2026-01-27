package dev.aurelium.slate.lore;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record LoreStyles(Map<Integer, String> styleMap) {

    @NotNull
    public String getStyle(int index) {
        return styleMap.getOrDefault(index, "");
    }
}
