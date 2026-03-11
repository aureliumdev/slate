package dev.aurelium.slate.fabric.item;

import dev.aurelium.slate.action.condition.Condition;
import dev.aurelium.slate.action.condition.ItemConditions;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.inv.MenuInventory;
import dev.aurelium.slate.item.TemplateItem;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class FabricTemplateItem<C> {

    private final TemplateItem<C> templateItem;
    private final FabricMenuItem bukkitMenuItem;

    public FabricTemplateItem(Slate slate, TemplateItem<C> templateItem) {
        this.templateItem = templateItem;
        this.bukkitMenuItem = new FabricMenuItem(slate, templateItem);
    }

    public boolean failsContextViewConditions(C context, ServerPlayer player, MenuInventory inventory) {
        return bukkitMenuItem.failsConditions(player, inventory, templateItem.getData().conditions().getOrDefault(context, ItemConditions.empty()).viewConditions());
    }

    public boolean failsContextClickConditions(C context, ClickTrigger trigger, ServerPlayer player, MenuInventory inventory) {
        List<Condition> conditions = templateItem.getData().conditions().getOrDefault(context, ItemConditions.empty()).clickConditions().getOrDefault(trigger, new ArrayList<>());
        return bukkitMenuItem.failsConditions(player, inventory, conditions);
    }
}
