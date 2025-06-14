package dev.aurelium.slate.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InventoryUtil {

    private static Method getOpenInventoryMethod;
    private static Method getTopInventoryMethod;
    private static Method getBottomInventoryMethod;
    private static boolean reflectionInitialized = false;
    private static boolean reflectionFailed = false;

    @Nullable
    public static Inventory getTopInventory(Player player) {
        if (VersionUtil.isAtLeastVersion(21)) {
            return player.getOpenInventory().getTopInventory();
        } else {
            if (reflectionFailed) return null;
            try {
                initReflection(player);
                return invokeInventory(player, getTopInventoryMethod);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                reflectionFailed = true;
            }
            return null;
        }
    }

    @Nullable
    public static Inventory getBottomInventory(Player player) {
        if (VersionUtil.isAtLeastVersion(21)) {
            return player.getOpenInventory().getBottomInventory();
        } else {
            if (reflectionFailed) return null;
            try {
                initReflection(player);
                return invokeInventory(player, getBottomInventoryMethod);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                reflectionFailed = true;
            }
            return null;
        }
    }

    private static Inventory invokeInventory(Player player, Method method) throws IllegalAccessException, InvocationTargetException {
        Object inventoryView = getOpenInventoryMethod.invoke(player);
        Object inventory = method.invoke(inventoryView);

        if (inventory instanceof Inventory) {
            return (Inventory) inventory;
        }
        return null;
    }

    private static void initReflection(Player player) throws IllegalAccessException, InvocationTargetException {
        if (reflectionInitialized) return;
        try {
            getOpenInventoryMethod = Player.class.getMethod("getOpenInventory");

            // We invoke getOpenInventory once to get the actual class of InventoryView (no interface reference)
            Object inventoryView = getOpenInventoryMethod.invoke(player);
            if (inventoryView == null) {
                reflectionFailed = true;
                return;
            }

            getTopInventoryMethod = inventoryView.getClass().getMethod("getTopInventory");
            getTopInventoryMethod.setAccessible(true);
            getBottomInventoryMethod = inventoryView.getClass().getMethod("getBottomInventory");
            getBottomInventoryMethod.setAccessible(true);

            reflectionInitialized = true;
        } catch (NoSuchMethodException e) {
            reflectionFailed = true;
        }
    }


}
