package dev.aurelium.slate.action.condition;

import dev.aurelium.slate.action.trigger.ClickTrigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ItemConditions(List<Condition> viewConditions, Map<ClickTrigger, List<Condition>> clickConditions) {

    public static ItemConditions empty() {
        return new ItemConditions(new ArrayList<>(), new HashMap<>());
    }

}
