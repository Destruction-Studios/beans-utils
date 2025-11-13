package net.ds.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.ds.config.ModServerConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class BeansUtilsCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        if (env.dedicated) {
            dispatcher.register(literal("beans-utils")
                    .requires(source -> source.hasPermissionLevel(4))
                    .then(literal("reload")
                            .executes(BeansUtilsCommands::reloadConfig)));
        }
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        ModServerConfig.INSTANCE.reloadConfigFile();
        context.getSource().sendMessage(Text.of("Reloaded BeansUtils config!"));
        return 1;
    }
}
