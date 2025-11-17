package net.ds.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.ds.tpa.TPACommands;
import net.ds.util.Util;
import net.ds.config.ModServerConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BeansUtilsCommands {
    private static final String[] FEATURES = {"netherPortalsDisabled", "endPortalsDisabled", "eyesOfEnderDisabled",};

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        TPACommands.registerTPACommands(dispatcher, registryAccess, env);
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
                    )
                    .then(literal("vanillaFeatures")
                            .then(argument("featureName", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        for (String f : FEATURES) {
                                            builder.suggest(f);
                                        }
                                        return builder.buildFuture();
                                    })
                                    .then(argument("value", BoolArgumentType.bool())
                                            .executes(BeansUtilsCommands::setVanillaFeature)
                                    )

                            )

                    )
            );
        }
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> context, String message) {
        context.getSource().sendFeedback(() -> Text.of(message), true);
    }

    private static int setVanillaFeature(CommandContext<ServerCommandSource> context) {
        String featureName = StringArgumentType.getString(context, "featureName");
        boolean value = BoolArgumentType.getBool(context, "value");
        boolean failed = false;

        switch (featureName) {
            case "netherPortalsDisabled" -> ModServerConfig.INSTANCE.setNetherPortalsDisabled(value);
            case "endPortalsDisabled" -> ModServerConfig.INSTANCE.setEndPortalsDisabled(value);
            case "eyesOfEnderDisabled" -> ModServerConfig.INSTANCE.setEyesOfEnderDisabled(value);
            default -> failed = true;
        }

        if (!failed) {
            ModServerConfig.INSTANCE.save();
            sendFeedback(context,"Set " + featureName + " = " + value);
            return 1;
        } else {
            sendFeedback(context, "Unknown feature name " + featureName);
            return -1;
        }
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        ModServerConfig.INSTANCE.reloadConfigFile();
        sendFeedback(context, "Reloaded BeansUtils config!");
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
        sendFeedback(context, "Successfully set resource pack url.");
        return 1;
    }

    private static int reloadAll(CommandContext<ServerCommandSource> context) {
        Util.reloadAll(context);
        sendFeedback(context, "Reloaded all players resource packs");
        return 1;
    }
}
