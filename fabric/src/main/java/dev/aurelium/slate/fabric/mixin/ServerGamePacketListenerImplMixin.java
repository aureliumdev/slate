package dev.aurelium.slate.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.aurelium.slate.fabric.event.InventoryEvents;
import dev.aurelium.slate.fabric.event.InventoryEvents.*;
import dev.aurelium.slate.util.Pair;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

    @WrapOperation(
            method = "handleContainerClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;clicked(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V"))
    private void handleContainerClick(AbstractContainerMenu instance, int slotNum, int buttonNum, ClickType clickType, Player player, Operation<Void> original,
            @Local(argsOnly = true) ServerboundContainerClickPacket packet) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            original.call(instance, slotNum, buttonNum, clickType, player);
            return;
        }

        ServerGamePacketListenerImpl thisListener = (ServerGamePacketListenerImpl) (Object) this;

        Container topInventory = getTopInventory(serverPlayer);
        Container bottomInventory = player.getInventory();

        Pair<ClickButton, InventoryAction> pair = getButtonAndAction(slotNum, packet, thisListener, topInventory, bottomInventory);
        ClickButton clickButton = pair.first();
        InventoryAction action = pair.second();

        Container clickedInventory;
        if (topInventory != null && slotNum >= 0 && slotNum < topInventory.getContainerSize()) {
            clickedInventory = topInventory;
        } else {
            clickedInventory = bottomInventory;
        }

        if (packet.clickType() != net.minecraft.world.inventory.ClickType.QUICK_CRAFT) {
            ItemStack currentItem = null;
            if (slotNum >= 0) {
                if (clickedInventory == topInventory) {
                    currentItem = topInventory.getItem(slotNum);
                } else if (clickedInventory == bottomInventory) {
                    int topSize = topInventory != null ? topInventory.getContainerSize() : 0;
                    currentItem = bottomInventory.getItem(slotNum - topSize);
                }
            }

            InventoryClickData clickData = new InventoryClickData(clickedInventory, clickButton, action, slotNum, currentItem);
            var cancellable = new Cancelable();
            InventoryEvents.CLICK.invoker().onClick(serverPlayer, clickData, cancellable);

            if (!cancellable.isCanceled()) {
                original.call(instance, slotNum, buttonNum, clickType, player);
            }
            // If canceled, we don't need to call anything
        } else {
            original.call(instance, slotNum, buttonNum, clickType, player);
        }
    }

    @Inject(method = "handleContainerClose", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;doCloseContainer()V"))
    private void handleContainerClose(ServerboundContainerClosePacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl thisListener = (ServerGamePacketListenerImpl) (Object) this;
        Container container = getTopInventory(thisListener.player);
        if (container == null) return;
        InventoryEvents.CLOSE.invoker().onClose(thisListener.player, CloseReason.PLAYER, container);
    }

    @Unique
    private @Nullable Container getTopInventory(ServerPlayer player) {
        Container topInventory = null;
        if (player.containerMenu instanceof ChestMenu chestMenu) {
            topInventory = chestMenu.getContainer();
        }
        return topInventory;
    }

    @Unique
    private Pair<ClickButton, InventoryAction> getButtonAndAction(int slotNum, ServerboundContainerClickPacket packet, ServerGamePacketListenerImpl thisListener,
            Container topInventory, Container bottomInventory) {
        ClickButton click = ClickButton.UNKNOWN;
        InventoryAction action = InventoryAction.UNKNOWN;

        switch (packet.clickType()) {
            case PICKUP:
                if (packet.buttonNum() == 0) {
                    click = ClickButton.LEFT;
                } else if (packet.buttonNum() == 1) {
                    click = ClickButton.RIGHT;
                }
                if (packet.buttonNum() == 0 || packet.buttonNum() == 1) {
                    action = InventoryAction.NOTHING; // Don't want to repeat ourselves
                    if (slotNum == net.minecraft.world.inventory.AbstractContainerMenu.SLOT_CLICKED_OUTSIDE) {
                        if (!thisListener.player.containerMenu.getCarried().isEmpty()) {
                            action = packet.buttonNum() == 0 ? InventoryAction.DROP_ALL_CURSOR : InventoryAction.DROP_ONE_CURSOR;
                        }
                    } else if (slotNum < 0)  {
                        action = InventoryAction.NOTHING;
                    } else {
                        Slot slot = thisListener.player.containerMenu.getSlot(slotNum);
                        if (slot != null) {
                            ItemStack clickedItem = slot.getItem();
                            ItemStack cursor = thisListener.player.containerMenu.getCarried();
                            if (clickedItem.isEmpty()) {
                                if (!cursor.isEmpty()) {
                                    // Removed unnecessary bundle logic
                                    action = packet.buttonNum() == 0 ? InventoryAction.PLACE_ALL : InventoryAction.PLACE_ONE;
                                }
                            } else if (slot.mayPickup(thisListener.player)) {
                                if (cursor.isEmpty()) {
                                    // Removed unnecessary bundle logic
                                    action = packet.buttonNum() == 0 ? InventoryAction.PICKUP_ALL : InventoryAction.PICKUP_HALF;
                                } else if (slot.mayPlace(cursor)) {
                                    if (ItemStack.isSameItemSameComponents(clickedItem, cursor)) {
                                        int toPlace = packet.buttonNum() == 0 ? cursor.getCount() : 1;
                                        toPlace = Math.min(toPlace, clickedItem.getMaxStackSize() - clickedItem.getCount());
                                        toPlace = Math.min(toPlace, slot.container.getMaxStackSize() - clickedItem.getCount());
                                        if (toPlace == 1) {
                                            action = InventoryAction.PLACE_ONE;
                                        } else if (toPlace == cursor.getCount()) {
                                            action = InventoryAction.PLACE_ALL;
                                        } else if (toPlace < 0) {
                                            action = toPlace != -1 ? InventoryAction.PICKUP_SOME : InventoryAction.PICKUP_ONE; // this happens with oversized stacks
                                        } else if (toPlace != 0) {
                                            action = InventoryAction.PLACE_SOME;
                                        }
                                    } else if (cursor.getCount() <= slot.getMaxStackSize()) {
                                        // Removed unnecessary bundle logic
                                        action = InventoryAction.SWAP_WITH_CURSOR;
                                    }
                                } else if (ItemStack.isSameItemSameComponents(cursor, clickedItem)) {
                                    if (clickedItem.getCount() >= 0) {
                                        if (clickedItem.getCount() + cursor.getCount() <= cursor.getMaxStackSize()) {
                                            // As of 1.5, this is result slots only
                                            action = InventoryAction.PICKUP_ALL;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case QUICK_MOVE:
                if (packet.buttonNum() == 0) {
                    click = ClickButton.SHIFT_LEFT;
                } else if (packet.buttonNum() == 1) {
                    click = ClickButton.SHIFT_RIGHT;
                }
                if (packet.buttonNum() == 0 || packet.buttonNum() == 1) {
                    if (slotNum < 0) {
                        action = InventoryAction.NOTHING;
                    } else {
                        Slot slot = thisListener.player.containerMenu.getSlot(slotNum);
                        if (slot != null && slot.mayPickup(thisListener.player) && slot.hasItem()) {
                            action = InventoryAction.MOVE_TO_OTHER_INVENTORY;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    }
                }
                break;
            case SWAP:
                if ((packet.buttonNum() >= 0 && packet.buttonNum() < 9) || packet.buttonNum() == Inventory.SLOT_OFFHAND) {
                    // Paper start - Add slot sanity checks to container clicks
                    if (slotNum < 0) {
                        action = InventoryAction.NOTHING;
                        break;
                    }
                    // Paper end - Add slot sanity checks to container clicks
                    click = (packet.buttonNum() == Inventory.SLOT_OFFHAND) ? ClickButton.SWAP_OFFHAND : ClickButton.NUMBER_KEY;
                    Slot clickedSlot = thisListener.player.containerMenu.getSlot(slotNum);
                    if (clickedSlot.mayPickup(thisListener.player)) {
                        ItemStack hotbar = thisListener.player.getInventory().getItem(packet.buttonNum());
                        if ((!hotbar.isEmpty() && clickedSlot.mayPlace(hotbar)) || (hotbar.isEmpty() && clickedSlot.hasItem())) { // Paper - modernify this logic (no such thing as a "hotbar move and readd"
                            action = InventoryAction.HOTBAR_SWAP;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    } else {
                        action = InventoryAction.NOTHING;
                    }
                }
                break;
            case CLONE:
                if (packet.buttonNum() == 2) {
                    click = ClickButton.MIDDLE;
                    if (slotNum < 0) {
                        action = InventoryAction.NOTHING;
                    } else {
                        Slot slot = thisListener.player.containerMenu.getSlot(slotNum);
                        if (slot != null && slot.hasItem() && thisListener.player.getAbilities().instabuild && thisListener.player.containerMenu.getCarried().isEmpty()) {
                            action = InventoryAction.CLONE_STACK;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    }
                } else {
                    click = ClickButton.UNKNOWN;
                    action = InventoryAction.UNKNOWN;
                }
                break;
            case THROW:
                if (slotNum >= 0) {
                    if (packet.buttonNum() == 0) {
                        click = ClickButton.DROP;
                        Slot slot = thisListener.player.containerMenu.getSlot(slotNum);
                        if (slot != null && slot.hasItem() && slot.mayPickup(thisListener.player) && !slot.getItem().isEmpty() && slot.getItem().getItem() != Items.AIR) {
                            action = InventoryAction.DROP_ONE_SLOT;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    } else if (packet.buttonNum() == 1) {
                        click = ClickButton.CONTROL_DROP;
                        Slot slot = thisListener.player.containerMenu.getSlot(slotNum);
                        if (slot != null && slot.hasItem() && slot.mayPickup(thisListener.player) && !slot.getItem().isEmpty() && slot.getItem().getItem() != Items.AIR) {
                            action = InventoryAction.DROP_ALL_SLOT;
                        } else {
                            action = InventoryAction.NOTHING;
                        }
                    }
                } else {
                    // Sane default (because this happens when they are holding nothing. Don't ask why.)
                    click = ClickButton.LEFT;
                    if (packet.buttonNum() == 1) {
                        click = ClickButton.RIGHT;
                    }
                    action = InventoryAction.NOTHING;
                }
                break;
            case PICKUP_ALL:
                click = ClickButton.DOUBLE_CLICK;
                action = InventoryAction.NOTHING;
                if (slotNum >= 0 && !thisListener.player.containerMenu.getCarried().isEmpty()) {
                    ItemStack cursor = thisListener.player.containerMenu.getCarried();
                    action = InventoryAction.NOTHING;
                    // Quick check for if we have any of the item
                    if (topInventory != null && topInventory.countItem(cursor.getItem()) > 0 || bottomInventory.countItem(cursor.getItem()) > 0) {
                        action = InventoryAction.COLLECT_TO_CURSOR;
                    }
                }
                break;
            default:
                break;
        }

        return new Pair<>(click, action);
    }
}
