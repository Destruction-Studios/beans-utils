package net.ds.tpa;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.ds.command.PlayerTPASuggestionProvider;
import net.ds.config.ModServerConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.world.TeleportTarget;

import java.util.*;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TPACommands {
    public static void registerTPACommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        if (!ModServerConfig.INSTANCE.getTpaEnabled()) {
            return;
        }
        TPA.register(dispatcher);
        TPAHere.register(dispatcher);
        TPAccept.register(dispatcher);
        TPACancel.register(dispatcher);
    }

    private static void sendFeedback(CommandContext<ServerCommandSource> context, Text text) {
        context.getSource().sendFeedback(() -> text, false);
    }
    private static void sendSound(ServerPlayerEntity player, SoundEvent soundEvent) {
        player.playSoundToPlayer(soundEvent, SoundCategory.MASTER, 1.0f, 1.0f);
    }

    public static class TPA {
        private static final String COMMAND = "tpa";
        public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(literal(COMMAND)
                    .then(argument("player", EntityArgumentType.player())
                            .executes(TPA::execute))
            );
        }

        private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
            if (!context.getSource().isExecutedByPlayer()) {
                return 1;
            }
            ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");

            UUID playerUUID = player.getUuid();
            String playerName = context.getSource().getPlayer().getName().getString();
            UUID targetUUID = targetPlayer.getUuid();
            String targetName = targetPlayer.getName().getString();

            if (playerUUID == targetUUID) {
                sendFeedback(context, Text.translatable("beans-utils.tpa.tp_self_error").formatted(Formatting.RED));
                return 1;
            }
            if (TPAManager.playerTPAHereMap.containsKey(targetUUID) && TPAManager.playerTPAHereMap.get(playerUUID).contains(playerUUID)) {
                sendFeedback(context, Text.translatable("beans-utils.tpa.tphere_already_called").formatted(Formatting.RED));
                return 1;
            }
            if (TPAManager.playerTPAMap.containsKey(targetUUID)) {
                if (TPAManager.playerTPAMap.get(playerUUID).contains(playerUUID)) {
                    sendFeedback(context, Text.translatable("beans-utils.tpa.tpa_already_called").formatted(Formatting.RED));
                    return 1;
                }

                TPAManager.playerTPAMap.get(targetUUID).add(playerUUID);
            } else {
                List<UUID> uuidList = new ArrayList<>();
                uuidList.add(playerUUID);
                TPAManager.playerTPAMap.put(targetUUID, uuidList);
            }
            targetPlayer.sendMessage(Text.translatable("beans-utils.tpa.tpa_request", playerName)
                    .formatted(Formatting.GOLD)
                    .styled(style -> style.withClickEvent(new ClickEvent.RunCommand("/tpaccept " + playerName)))
            );
            sendSound(player, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE);
            sendFeedback(context, Text.translatable("beans-utils.tpa.tpa_sent").withColor(Colors.GREEN));

            TPAManager.addRunnable(() -> {
                if (TPAManager.playerTPAMap.containsKey(targetUUID)) {
                    if (TPAManager.playerTPAMap.get(targetUUID).contains(playerUUID)) {
                        TPAManager.playerTPAMap.get(targetUUID).remove(playerUUID);
                        if (TPAManager.playerTPAMap.get(targetUUID).isEmpty()) {
                            TPAManager.playerTPAMap.remove(targetUUID);
                        }
                        sendFeedback(context, Text.translatable("beans-utils.tpa.tpa_expire").formatted(Formatting.RED));
                        sendSound(player, SoundEvents.ENTITY_VILLAGER_NO);
                    }
                }
            });

            return 1;
        }
    }

    public static class TPAHere {
        private static final String COMMAND = "tpahere";
        public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(literal(COMMAND)
                    .then(argument("player", EntityArgumentType.player())
                            .executes(TPAHere::execute))
            );
        }

        private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
            if (!context.getSource().isExecutedByPlayer()) {
                return 1;
            }
            ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");

            UUID playerUUID = player.getUuid();
            String playerName = context.getSource().getPlayer().getName().getString();
            UUID targetUUID = targetPlayer.getUuid();
            String targetName = targetPlayer.getName().getString();

            if (playerUUID == targetUUID) {
                sendFeedback(context, Text.translatable("beans-utils.tpa.tp_self_error").formatted(Formatting.RED));
                return 1;
            }
            if (TPAManager.playerTPAMap.containsKey(targetUUID) && TPAManager.playerTPAMap.get(targetUUID).contains(playerUUID)) {
                sendFeedback(context, Text.translatable("beans-utils.tpa.tpa_already_called").formatted(Formatting.RED));
                return 1;
            }
            if (TPAManager.playerTPAHereMap.containsKey(targetUUID)) {
                if (TPAManager.playerTPAHereMap.get(targetUUID).contains(playerUUID)) {
                    sendFeedback(context, Text.translatable("beans-utils.tpa.tpahere_already_called").formatted(Formatting.RED));
                    return 1;
                }
                TPAManager.playerTPAHereMap.get(targetUUID).add(playerUUID);
            } else {
                List<UUID> uuidList = new ArrayList<>();
                uuidList.add(playerUUID);
                TPAManager.playerTPAHereMap.put(targetUUID, uuidList);
            }

            targetPlayer.sendMessage(Text.translatable("beans-utils.tpa.tpahere_request", playerName)
                    .formatted(Formatting.GOLD)
                    .styled(style -> style.withClickEvent(new ClickEvent.RunCommand("/tpaccept " + playerName)))
            );
            sendSound(targetPlayer, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE);
            sendFeedback(context, Text.translatable("beans-utils.tpa.tpahere_sent").formatted(Formatting.GREEN));

            TPAManager.addRunnable(() -> {
                if (TPAManager.playerTPAHereMap.containsKey(targetUUID)) {
                    if (TPAManager.playerTPAHereMap.get(targetUUID).contains(playerUUID)) {
                        TPAManager.playerTPAHereMap.get(targetUUID).remove(playerUUID);
                        if (TPAManager.playerTPAHereMap.get(targetUUID).isEmpty()) {
                            TPAManager.playerTPAHereMap.remove(targetUUID);
                        }
                        sendFeedback(context, Text.translatable("beans-utils.tpa.tpahere_expire").formatted(Formatting.RED));
                        sendSound(player, SoundEvents.ENTITY_VILLAGER_NO);
                    }
                }
            });

            return 1;
        }

    }

    public static class TPAccept {
        private static final String COMMAND = "tpaccept";

        public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(literal(COMMAND)
                    .then(argument("player", EntityArgumentType.player())
                            .suggests(new PlayerTPASuggestionProvider())
                            .executes(TPAccept::execute)));
        }

        private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
            if (!context.getSource().isExecutedByPlayer()) {
                return 1;
            }
            ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
            UUID playerUUID = EntityArgumentType.getPlayer(context, "player").getUuid();

            if (TPAManager.playerTPAMap.containsKey(player.getUuid())) {
                if (!TPAManager.playerTPAMap.get(player.getUuid()).contains(playerUUID)) {
                    sendFeedback(context, Text.translatable("beans-utils.tpa.tpaccept_error").formatted(Formatting.RED));
                    return 1;
                }
                UUID targetUUID = TPAManager.playerTPAMap.get(player.getUuid()).remove(TPAManager.playerTPAMap.get(player.getUuid()).indexOf(playerUUID));
                ServerPlayerEntity targetPlayer = Objects.requireNonNull(context.getSource().getServer().getPlayerManager().getPlayer(targetUUID));
                TeleportTarget teleportTarget = new TeleportTarget(
                        player.getEntityWorld(),
                        player.getBlockPos().toCenterPos(),
                        targetPlayer.getVelocity(),
                        targetPlayer.getYaw(),
                        targetPlayer.getPitch(),
                        TeleportTarget.ADD_PORTAL_CHUNK_TICKET
                );
                targetPlayer.teleportTo(teleportTarget);
                if (TPAManager.playerTPAMap.get(player.getUuid()).isEmpty()) {
                    TPAManager.playerTPAMap.remove(player.getUuid());
                }
                targetPlayer.sendMessage(Text.translatable("beans-utils.tpa.tpa_success", targetPlayer.getName().getString()).formatted(Formatting.GREEN));
                sendSound(targetPlayer, SoundEvents.ENTITY_ENDER_EYE_DEATH);
            } else if (TPAManager.playerTPAHereMap.containsKey(player.getUuid())) {
                if (!TPAManager.playerTPAHereMap.get(player.getUuid()).contains(playerUUID)){
                    sendFeedback(context, Text.translatable("beans-utils.tpa.tpaccept_error").formatted(Formatting.RED));
                    return 1;
                }
                UUID targetUuid = TPAManager.playerTPAHereMap.get(player.getUuid()).remove(TPAManager.playerTPAHereMap.get(player.getUuid()).indexOf(playerUUID));
                ServerPlayerEntity target_player = Objects.requireNonNull(context.getSource().getServer().getPlayerManager().getPlayer(targetUuid));
                TeleportTarget teleport_target = new TeleportTarget(target_player.getEntityWorld(), target_player.getBlockPos().toCenterPos(),player.getVelocity(),player.getYaw(),player.getPitch(),TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
                player.teleportTo(teleport_target);
                if (TPAManager.playerTPAHereMap.get(player.getUuid()).isEmpty()){
                    TPAManager.playerTPAHereMap.remove(player.getUuid());
                }
                player.sendMessage(Text.translatable("beans-utils.tpa.tpahere_success", target_player.getName().getString()).formatted(Formatting.GREEN));
                sendSound(player, SoundEvents.ENTITY_ENDER_EYE_DEATH);
            } else {
                sendFeedback(context, Text.translatable("beans-utils.tpa.tpaccept_error").formatted(Formatting.RED));
            }

            return 1;
        }
    }

    public static class TPACancel {
        private static final String COMMAND = "tpacancel";

        public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(literal(COMMAND)
                    .executes(TPACancel::execute));
        }

        private static int execute(CommandContext<ServerCommandSource> context) {
            UUID playerUUID = Objects.requireNonNull(context.getSource().getPlayer()).getUuid();
            List<UUID> keysTPA = Collections.list(TPAManager.playerTPAMap.keys());
            List<UUID> keysTPAHere = Collections.list(TPAManager.playerTPAHereMap.keys());

            for (UUID targetUUID : keysTPA) {
                TPAManager.playerTPAMap.get(targetUUID).remove(playerUUID);
                if (TPAManager.playerTPAMap.get(targetUUID).isEmpty()) {
                    TPAManager.playerTPAMap.remove(targetUUID);
                }
            }
            for (UUID targetUUID : keysTPAHere) {
                TPAManager.playerTPAHereMap.get(targetUUID).remove(playerUUID);
                if (TPAManager.playerTPAHereMap.get(targetUUID).isEmpty()) {
                    TPAManager.playerTPAHereMap.remove(targetUUID);
                }
            }
            sendFeedback(context, Text.translatable("beans-utils.tpa.tpa_cancel_success").formatted(Formatting.YELLOW));
            return 1;
        }
    }
}
