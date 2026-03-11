package dev.aurelium.slate.fabric.inv;

import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.inv.content.SlotPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public record ItemClickData(ServerPlayer player, ItemStack item, SlotPos slot, Set<ClickTrigger> clickTriggers) {

}
