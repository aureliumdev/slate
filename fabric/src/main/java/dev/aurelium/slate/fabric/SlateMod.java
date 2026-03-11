package dev.aurelium.slate.fabric;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.aurelium.slate.fabric.option.SlateOptions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SlateMod implements ModInitializer {

    public static SlateMod INSTANCE;
    public static final String MOD_ID = "slate";
    private final File pluginFolder;
    private Slate slate;
    private final List<Slate> otherSlateInstances = new ArrayList<>();

    public SlateMod() {
        INSTANCE = this;
        this.pluginFolder = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toFile();
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::enable);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("slate")
                        .then(literal("open")
                                .then(argument("menu", StringArgumentType.string())
                                        .executes(context ->
                                                handleOpenMenu(context, StringArgumentType.getString(context, "menu")))))));
    }

    public void addOtherSlateInstance(Slate slate) {
        this.otherSlateInstances.add(slate);
    }

    private void enable(MinecraftServer server) {
        SlateOptions options = SlateOptions.builder()
                .mainDirectory(pluginFolder)
                .build();
        this.slate = new Slate(server, options);
        slate.loadMenus();
    }

    private int handleOpenMenu(CommandContext<CommandSourceStack> context, String menuName) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (slate.getLoadedMenu(menuName) != null) {
            slate.openMenu(player, menuName);
        } else {
            for (Slate otherSlateInstance : otherSlateInstances) {
                if (otherSlateInstance.getLoadedMenu(menuName) != null) {
                    otherSlateInstance.openMenu(player, menuName);
                    break;
                }
            }
        }
        return 1;
    }
}
