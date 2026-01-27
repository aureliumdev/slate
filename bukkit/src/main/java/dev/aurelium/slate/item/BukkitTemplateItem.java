package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.condition.Condition;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.menu.MenuInventory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BukkitTemplateItem<C> {

    private final TemplateItem<C> templateItem;
    private final BukkitMenuItem bukkitMenuItem;

    public BukkitTemplateItem(Slate slate, TemplateItem<C> templateItem) {
        this.templateItem = templateItem;
        this.bukkitMenuItem = new BukkitMenuItem(slate, templateItem);
    }

    public boolean failsContextViewConditions(C context, Player player, MenuInventory inventory) {
        return bukkitMenuItem.failsConditions(player, inventory, templateItem.getData().conditions().getOrDefault(context, ItemConditions.empty()).viewConditions());
    }

    public boolean failsContextClickConditions(C context, ClickTrigger trigger, Player player, MenuInventory inventory) {
        List<Condition> conditions = templateItem.getData().conditions().getOrDefault(context, ItemConditions.empty()).clickConditions().getOrDefault(trigger, new ArrayList<>());
        return bukkitMenuItem.failsConditions(player, inventory, conditions);
    }
}
