package dev.aurelium.slate.action;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.inv.content.InventoryContents;
import dev.aurelium.slate.menu.MenuInventory;
import dev.aurelium.slate.util.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandAction extends Action {

    private final String command;
    private final Executor executor;

    public CommandAction(Slate slate, String command, Executor executor) {
        super(slate);
        this.command = command;
        this.executor = executor;
    }

    @Override
    public void execute(Player player, MenuInventory menuInventory, InventoryContents contents) {
        String formattedCommand = formatCommand(player, command);
        if (executor == Executor.CONSOLE) {
            slate.getScheduler().runGlobal(() -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), formattedCommand));
        } else if (executor == Executor.PLAYER) {
            slate.getScheduler().run(player, () -> player.performCommand(formattedCommand));
        }
    }

    private String formatCommand(Player player, String command) {
        command = TextUtil.replace(command, "{player}", player.getName());
        if (slate.isPlaceholderAPIEnabled()) {
            command = PlaceholderAPI.setPlaceholders(player, command);
        }
        return command;
    }

    public enum Executor {

        CONSOLE,
        PLAYER

    }

}
