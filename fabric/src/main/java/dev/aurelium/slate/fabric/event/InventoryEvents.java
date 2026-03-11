package dev.aurelium.slate.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class InventoryEvents {

    public static final Event<InventoryClick> CLICK = EventFactory.createArrayBacked(InventoryClick.class, callbacks -> (player, data, cancelable) -> {
        for (InventoryClick callback : callbacks) {
            callback.onClick(player, data, cancelable);
        }
    });
    public static final Event<InventoryClose> CLOSE = EventFactory.createArrayBacked(InventoryClose.class, callbacks -> (player, reason, container) -> {
        for (InventoryClose callback : callbacks) {
            callback.onClose(player, reason, container);
        }
    });

    public interface InventoryClick {

        void onClick(ServerPlayer player, InventoryClickData data, Cancelable cancelable);
    }

    public interface InventoryClose {

        void onClose(ServerPlayer player, CloseReason reason, Container container);
    }

    public record InventoryClickData(
            Container clickedInventory,
            ClickButton clickButton,
            InventoryAction action,
            int slot,
            @Nullable ItemStack currentItem) {
    }

    public static class Cancelable {

        private boolean canceled = false;

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }
    }

    // Ported from org.bukkit.event.inventory.InventoryAction
    public enum InventoryAction {

        NOTHING,
        PICKUP_ALL,
        PICKUP_SOME,
        PICKUP_HALF,
        PICKUP_ONE,
        PLACE_ALL,
        PLACE_SOME,
        PLACE_ONE,
        SWAP_WITH_CURSOR,
        DROP_ALL_CURSOR,
        DROP_ONE_CURSOR,
        DROP_ALL_SLOT,
        DROP_ONE_SLOT,
        MOVE_TO_OTHER_INVENTORY,
        HOTBAR_SWAP,
        CLONE_STACK,
        COLLECT_TO_CURSOR,
        UNKNOWN,
        PICKUP_FROM_BUNDLE,
        PICKUP_ALL_INTO_BUNDLE,
        PICKUP_SOME_INTO_BUNDLE,
        PLACE_FROM_BUNDLE,
        PLACE_ALL_INTO_BUNDLE,
        PLACE_SOME_INTO_BUNDLE
    }

    // Ported from org.bukkit.event.inventory.ClickType
    public enum ClickButton {

        /**
         * The left (or primary) mouse button.
         */
        LEFT,
        /**
         * Holding shift while pressing the left mouse button.
         */
        SHIFT_LEFT,
        /**
         * The right mouse button.
         */
        RIGHT,
        /**
         * Holding shift while pressing the right mouse button.
         */
        SHIFT_RIGHT,
        /**
         * Clicking the left mouse button on the grey area around the inventory.
         */
        WINDOW_BORDER_LEFT,
        /**
         * Clicking the right mouse button on the grey area around the inventory.
         */
        WINDOW_BORDER_RIGHT,
        /**
         * The middle mouse button, or a "scrollwheel click".
         */
        MIDDLE,
        /**
         * One of the number keys 1-9, correspond to slots on the hotbar.
         */
        NUMBER_KEY,
        /**
         * Pressing the left mouse button twice in quick succession.
         */
        DOUBLE_CLICK,
        /**
         * The "Drop" key (defaults to Q).
         */
        DROP,
        /**
         * Holding Ctrl while pressing the "Drop" key (defaults to Q).
         */
        CONTROL_DROP,
        /**
         * Any action done with the Creative inventory open.
         */
        CREATIVE,
        /**
         * The "swap item with offhand" key (defaults to F).
         */
        SWAP_OFFHAND,
        /**
         * A type of inventory manipulation not yet recognized by Bukkit.
         * <p>
         * This is only for transitional purposes on a new Minecraft update, and
         * should never be relied upon.
         * <p>
         * Any ClickType.UNKNOWN is called on a best-effort basis.
         */
        UNKNOWN,
        ;

        /**
         * Gets whether this ClickType represents the pressing of a key on a
         * keyboard.
         *
         * @return {@code true} if this ClickType represents the pressing of a key
         */
        public boolean isKeyboardClick() {
            return (this == ClickButton.NUMBER_KEY) || (this == ClickButton.DROP) || (this == ClickButton.CONTROL_DROP) || (this == ClickButton.SWAP_OFFHAND);
        }

        /**
         * Gets whether this ClickType represents the pressing of a mouse button
         *
         * @return {@code true} if this ClickType represents the pressing of a mouse button
         */
        public boolean isMouseClick() {
            return (this == ClickButton.DOUBLE_CLICK) || (this == ClickButton.LEFT) || (this == ClickButton.RIGHT) || (this == ClickButton.MIDDLE)
                    || (this == ClickButton.WINDOW_BORDER_LEFT) || (this == ClickButton.SHIFT_LEFT) || (this == ClickButton.SHIFT_RIGHT) || (this == ClickButton.WINDOW_BORDER_RIGHT);
        }

        /**
         * Gets whether this ClickType represents an action that can only be
         * performed by a Player in creative mode.
         *
         * @return {@code true} if this action requires Creative mode
         */
        public boolean isCreativeAction() {
            // Why use middle click?
            return (this == ClickButton.MIDDLE) || (this == ClickButton.CREATIVE);
        }

        /**
         * Gets whether this ClickType represents a right click.
         *
         * @return {@code true} if this ClickType represents a right click
         */
        public boolean isRightClick() {
            return (this == ClickButton.RIGHT) || (this == ClickButton.SHIFT_RIGHT);
        }

        /**
         * Gets whether this ClickType represents a left click.
         *
         * @return {@code true} if this ClickType represents a left click
         */
        public boolean isLeftClick() {
            return (this == ClickButton.LEFT) || (this == ClickButton.SHIFT_LEFT) || (this == ClickButton.DOUBLE_CLICK) || (this == ClickButton.CREATIVE);
        }

        /**
         * Gets whether this ClickType indicates that the shift key was pressed
         * down when the click was made.
         *
         * @return {@code true} if the action uses Shift.
         */
        public boolean isShiftClick() {
            return (this == ClickButton.SHIFT_LEFT) || (this == ClickButton.SHIFT_RIGHT);
        }
    }

    public enum CloseReason {

        PLAYER,
        DISCONNECT,
        DEATH,
        UNKNOWN

    }
}
