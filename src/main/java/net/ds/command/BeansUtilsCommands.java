package net.ds.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.ds.util.Util;
import net.ds.config.ModServerConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BeansUtilsCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        if (env.dedicated) {
            dispatcher.register(literal("beans-utils")
                    .requires(source -> source.hasPermissionLevel(4))
                    .then(literal("reload")
                            .executes(BeansUtilsCommands::reloadConfig)
                    )
                    .then(literal("resources")
                            .then(literal("fetchHash").executes(BeansUtilsCommands::fetchHash))
                            .then(literal("reloadPlayerResourcePacks").executes(BeansUtilsCommands::reloadAll))
                            .then(literal("setUrl").then(argument("url", StringArgumentType.greedyString()).executes(BeansUtilsCommands::setUrl)))
                    ));
        }
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        ModServerConfig.INSTANCE.reloadConfigFile();
        context.getSource().sendMessage(Text.of("Reloaded BeansUtils config!"));
        return 1;
    }

    private static int fetchHash(CommandContext<ServerCommandSource> context) {
        Util.fetchHash(context.getSource());
        return 1;
    }

    private static int setUrl(CommandContext<ServerCommandSource> context) {
        String url = StringArgumentType.getString(context,"url");
        ModServerConfig.INSTANCE.setCustomResourcePackURL(url);
        ModServerConfig.INSTANCE.save();
        context.getSource().sendMessage(Text.of("Set url."));
        return 1;
    }

    private static int reloadAll(CommandContext<ServerCommandSource> context) {
        Util.reloadAll(context);
        context.getSource().sendMessage(Text.of("Reloaded all players resource packs"));
        return 1;
    }
}
