package dev.aurelium.slate.action;

import dev.aurelium.slate.action.trigger.ClickTrigger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ItemActions(Map<ClickTrigger, List<Action>> actions) {

    public static ItemActions empty() {
        return new ItemActions(new LinkedHashMap<>());
    }

}
