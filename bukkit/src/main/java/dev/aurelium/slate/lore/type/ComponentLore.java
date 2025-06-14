package dev.aurelium.slate.lore.type;

import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.lore.LoreType;

public class ComponentLore extends LoreLine {

    private final String component;

    public ComponentLore(String component) {
        super(LoreType.COMPONENT);
        this.component = component;
    }

    public String getComponent() {
        return component;
    }
}
