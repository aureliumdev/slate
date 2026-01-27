package dev.aurelium.slate.menu;

import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.trigger.MenuTrigger;
import dev.aurelium.slate.component.MenuComponent;
import dev.aurelium.slate.fill.FillData;
import dev.aurelium.slate.item.MenuItem;

import java.util.List;
import java.util.Map;

public record LoadedMenu(
        String name,
        String title,
        int size,
        Map<String, MenuItem> items,
        Map<String, MenuComponent> components,
        Map<String, String> formats,
        FillData fillData,
        Map<String, Object> options,
        Map<MenuTrigger, List<Action>> actions
) {

}
