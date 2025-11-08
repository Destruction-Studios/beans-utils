package net.ds;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.ds.config.BeansUtilsServerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.NoSuchElementException;

import static net.minecraft.server.command.CommandManager.literal;

public class BeansUtilsCommands {
    private static final BeansUtilsServerConfig SERVER_CONFIG = BeansUtils.SERVER_CONFIG;

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        if (env.dedicated) {
            updateHash.register(dispatcher);
        }
    }

    private static void sendMessage(CommandContext<ServerCommandSource> context, Text message, boolean log) {
        context.getSource().sendMessage(message);
        if (log) {
            BeansUtils.LOGGER.info(message.getString());
        }
    }

    public static class updateHash {
        public static final String command = "updateHash";

        public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(literal(command)
                    .requires(source -> source.hasPermissionLevel(
                            4
                    ))
                    .executes(updateHash::execute)
            );
        }

        private static int execute(CommandContext<ServerCommandSource> context) {
            return 1;
        }

    }
}
