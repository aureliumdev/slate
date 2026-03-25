package dev.aurelium.slate.fabric.inv;

import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.event.InventoryEvents;
import dev.aurelium.slate.fabric.event.InventoryEvents.*;
import dev.aurelium.slate.fabric.inv.content.InventoryContents;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.scheduler.WrappedTask;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class InventoryManager {

    private final Slate slate;
    private final Map<UUID, SlateInventory> inventories;
    private final Map<UUID, InventoryContents> contents;
    private final Map<UUID, WrappedTask> updateTasks;

    public InventoryManager(Slate slate) {
        this.slate = slate;
        this.inventories = new ConcurrentHashMap<>();
        this.contents = new ConcurrentHashMap<>();
        this.updateTasks = new ConcurrentHashMap<>();
        new InvListener();
    }

    public @Nullable SlateInventory getInventory(ServerPlayer player) {
        return inventories.get(player.getUUID());
    }

    public void setInventory(ServerPlayer player, SlateInventory inventory) {
        if (inventory == null) {
            inventories.remove(player.getUUID());
        } else {
            inventories.put(player.getUUID(), inventory);
        }
    }

    public Map<UUID, SlateInventory> getInventories() {
        return inventories;
    }

    public Set<UUID> getOpenedPlayers(SlateInventory inv) {
        Set<UUID> list = new HashSet<>();

        for (Entry<UUID, SlateInventory> entry : this.inventories.entrySet()) {
            UUID uuid = entry.getKey();
            SlateInventory playerInv = entry.getValue();
            if (inv.equals(playerInv)) {
                list.add(uuid);
            }
        }

        return list;
    }

    public Optional<InventoryContents> getContents(ServerPlayer p) {
        return Optional.ofNullable(this.contents.get(p.getUUID()));
    }

    public void scheduleUpdateTask(ServerPlayer player, SlateInventory inv) {
        final InventoryContents inventoryContents = contents.get(player.getUUID());

        if (inventoryContents != null) {
            WrappedTask task = slate.getScheduler().timerSync(() -> inv.getMenuInventory().update(player, inventoryContents),
                    50, 50, TimeUnit.MILLISECONDS);
            this.updateTasks.put(player.getUUID(), task);
        }
    }

    protected void cancelUpdateTask(ServerPlayer p) {
        if (updateTasks.containsKey(p.getUUID())) {
            this.updateTasks.get(p.getUUID()).cancel();
            this.updateTasks.remove(p.getUUID());
        }
    }

    protected void setContents(ServerPlayer p, InventoryContents contents) {
        if (contents == null)
            this.contents.remove(p.getUUID());
        else
            this.contents.put(p.getUUID(), contents);
    }

    class InvListener {

        InvListener() {
            InventoryEvents.CLICK.register(this::onClick);
            InventoryEvents.CLOSE.register(this::onClose);
            ServerPlayerEvents.LEAVE.register(this::onLeave);
            ServerLifecycleEvents.SERVER_STOPPING.register(this::onDisable);
        }

        private void onClose(ServerPlayer player, CloseReason reason, Container container) {
            if (!inventories.containsKey(player.getUUID())) return;

            container.clearContent();
            inventories.remove(player.getUUID());
            contents.remove(player.getUUID());
        }

        private void onClick(ServerPlayer player, InventoryClickData data, Cancelable cancelable) {
            SlateInventory inv = inventories.get(player.getUUID());
            if (inv == null) return;

            AbstractContainerMenu containerMenu = player.containerMenu;
            Container topInventory = null;
            if (containerMenu instanceof ChestMenu chestMenu) {
                topInventory = chestMenu.getContainer();
            }

            if (topInventory == null) {
                player.closeContainer();
                throw new RuntimeException("topInventory should not be null");
            }

            Container clickedInventory = data.clickedInventory();
            if (clickedInventory == player.getInventory()) { // Bottom inventory
                if (data.action() == InventoryAction.COLLECT_TO_CURSOR || data.action() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    cancelable.setCanceled(true);
                    return;
                }

                if (data.action() == InventoryAction.NOTHING && data.clickButton() != ClickButton.MIDDLE) {
                    cancelable.setCanceled(true);
                    return;
                }
            }

            if (clickedInventory == topInventory) {
                cancelable.setCanceled(true);

                int row = data.slot() / 9;
                int column = data.slot() % 9;

                if (!inv.checkBounds(row, column))
                    return;

                InventoryContents invContents = contents.get(player.getUUID());
                SlotPos slot = SlotPos.of(row, column);

                if (data.currentItem() == null) return;

                invContents.get(slot)
                        .ifPresent(item -> item.run(new ItemClickData(player, data.currentItem(), slot, getClickTriggers(data))));

                player.containerMenu.sendAllDataToRemote();
            }
        }

        private Set<ClickTrigger> getClickTriggers(InventoryClickData data) {
            Set<ClickTrigger> triggers = new HashSet<>();
            triggers.add(ClickTrigger.ANY);

            ClickButton button = data.clickButton();
            if (button.isLeftClick()) {
                triggers.add(ClickTrigger.LEFT);
            } else if (button.isRightClick()) {
                triggers.add(ClickTrigger.RIGHT);
            } else if (button == ClickButton.MIDDLE) {
                triggers.add(ClickTrigger.MIDDLE);
            } else if (button == ClickButton.DROP || button == ClickButton.CONTROL_DROP) {
                triggers.add(ClickTrigger.DROP);
            }
            return triggers;
        }

        private void onDisable(MinecraftServer minecraftServer) {
            new HashMap<>(inventories).forEach((playerId, inv) -> {
                ServerPlayer player = minecraftServer.getPlayerList().getPlayer(playerId);
                if (player != null) {
                    inv.close(player);
                }
            });

            inventories.clear();
            contents.clear();
        }

        private void onLeave(ServerPlayer player) {
            if (!inventories.containsKey(player.getUUID())) return;

            inventories.remove(player.getUUID());
            contents.remove(player.getUUID());
            player.containerMenu.sendAllDataToRemote();
        }

    }
}
