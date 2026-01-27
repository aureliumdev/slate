package dev.aurelium.slate.action;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.CommandAction.Executor;
import dev.aurelium.slate.action.MenuAction.ActionType;
import dev.aurelium.slate.builder.BuiltMenu;
import dev.aurelium.slate.info.MenuInfo;
import dev.aurelium.slate.menu.MenuInventory;
import dev.aurelium.slate.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static dev.aurelium.slate.bukkit.ref.BukkitPlayerRef.wrap;

public class BukkitActionExecutor {

    private final Slate slate;

    public BukkitActionExecutor(Slate slate) {
        this.slate = slate;
    }

    public void executeAction(Action action, Player player, MenuInventory menuInventory) {
        if (action instanceof CommandAction commandAction) {
            executeCommand(commandAction, player);
        } else if (action instanceof MenuAction menuAction) {
            executeMenu(menuAction, player, menuInventory);
        } else if (action instanceof SoundAction soundAction) {
            executeSound(soundAction, player);
        }
    }

    private void executeSound(SoundAction action, Player player) {
        player.playSound(player.getLocation(), action.getSound(), SoundCategory.valueOf(action.getCategory().name()), action.getVolume(), action.getPitch());
    }

    private void executeCommand(CommandAction action, Player player) {
        String formattedCommand = formatCommand(player, action.getCommand());
        Executor executor = action.getExecutor();
        if (executor == Executor.CONSOLE) {
            slate.getScheduler().runGlobal(() -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), formattedCommand));
        } else if (executor == Executor.PLAYER) {
            slate.getScheduler().run(player, () -> player.performCommand(formattedCommand));
        }
    }

    private String formatCommand(Player player, String command) {
        command = TextUtil.replace(command, "{player}", player.getName());
        if (slate.isPlaceholderAPIEnabled()) {
            command = slate.getPlaceholderHook().setPlaceholders(wrap(player), command);
        }
        return command;
    }

    private void executeMenu(MenuAction action, Player player, MenuInventory menuInventory) {
        ActionType actionType = action.getActionType();
        String menuName = action.getMenuName();
        switch (actionType) {
            case OPEN:
                slate.openMenu(player, menuName, getProperties(menuInventory, action));
                break;
            case CLOSE:
                player.closeInventory();
                break;
            case NEXT_PAGE:
                int nextPage = menuInventory.getCurrentPage() + 1;
                if (nextPage < menuInventory.getTotalPages()) {
                    slate.openMenu(player, menuInventory.getMenu().name(), getProperties(menuInventory, action), nextPage);
                }
                break;
            case PREVIOUS_PAGE:
                int previousPage = menuInventory.getCurrentPage() - 1;
                if (previousPage >= 0) {
                    slate.openMenu(player, menuInventory.getMenu().name(), getProperties(menuInventory, action), previousPage);
                }
                break;
        }

    }

    private Map<String, Object> getProperties(MenuInventory inventory, MenuAction action) {
        // Add BuiltMenu properties from PropertyProvider
        BuiltMenu builtMenu = slate.getBuiltMenu(action.getMenuName());
        MenuInfo info = new MenuInfo(slate, inventory.getPlayer(), inventory.getActiveMenu());
        Map<String, Object> base = new HashMap<>(builtMenu.propertyProvider().get(info));
        // Otherwise fallback to current menu properties
        if (base.isEmpty()) {
            base.putAll(inventory.getProperties());
        }
        // Override with action-defined properties
        base.putAll(action.getProperties());
        return base;
    }

}
