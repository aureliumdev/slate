package dev.aurelium.slate.fabric.action;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.CommandAction;
import dev.aurelium.slate.action.CommandAction.Executor;
import dev.aurelium.slate.action.MenuAction;
import dev.aurelium.slate.action.MenuAction.ActionType;
import dev.aurelium.slate.action.SoundAction;
import dev.aurelium.slate.fabric.Slate;
import dev.aurelium.slate.fabric.builder.BuiltMenu;
import dev.aurelium.slate.fabric.info.MenuInfo;
import dev.aurelium.slate.fabric.inv.MenuInventory;
import dev.aurelium.slate.util.TextUtil;
import net.kyori.adventure.sound.Sound;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;


public class FabricActionExecutor {

    private final Slate slate;

    public FabricActionExecutor(Slate slate) {
        this.slate = slate;
    }

    public void executeAction(Action action, ServerPlayer player, MenuInventory menuInventory) {
        if (action instanceof CommandAction commandAction) {
            executeCommand(commandAction, player);
        } else if (action instanceof MenuAction menuAction) {
            executeMenu(menuAction, player, menuInventory);
        } else if (action instanceof SoundAction soundAction) {
            executeSound(soundAction, player);
        }
    }

    private void executeSound(SoundAction action, ServerPlayer player) {
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(action.getSound()));
        if (soundEvent == null) {
            return;
        }

        Sound sound = Sound.sound(soundEvent, action.getCategory(), action.getVolume(), action.getPitch());
        player.playSound(sound);
    }

    private void executeCommand(CommandAction action, ServerPlayer player) {
        String formattedCommand = formatCommand(player, action.getCommand());
        Executor executor = action.getExecutor();
        if (executor == Executor.CONSOLE) {
            slate.getScheduler().executeSync(() -> dispatchCommand(slate.getServer().createCommandSourceStack(), formattedCommand));
        } else if (executor == Executor.PLAYER) {
            slate.getScheduler().executeSync(() -> dispatchCommand(player.createCommandSourceStack(), formattedCommand));
        }
    }

    private void dispatchCommand(CommandSourceStack sourceStack, String formattedCommand) {
        Commands commands = slate.getServer().getCommands();
        ParseResults<CommandSourceStack> results = commands.getDispatcher().parse(formattedCommand, sourceStack);
        if (results.getContext().getNodes().isEmpty()) {
            return;
        }
        try {
            Commands.validateParseResults(results);
            commands.performCommand(results, formattedCommand);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    private String formatCommand(ServerPlayer player, String command) {
        return StringUtils.normalizeSpace(TextUtil.replace(command, "{player}", player.getPlainTextName())).trim();
    }

    private void executeMenu(MenuAction action, ServerPlayer player, MenuInventory menuInventory) {
        ActionType actionType = action.getActionType();
        String menuName = action.getMenuName();
        switch (actionType) {
            case OPEN:
                slate.openMenu(player, menuName, getProperties(menuInventory, action));
                break;
            case CLOSE:
                player.closeContainer();
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
