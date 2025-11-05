package net.ds;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.ds.config.BeansUtilsServerConfig;
import net.ds.events.EndTick;
import net.ds.network.CombatPayload;
import net.ds.network.HandshakePayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BeansUtils implements ModInitializer {
    public static final String MOD_ID = "beans-utils";
    public static final String MOD_VERSION = FabricLoader.getInstance()
            .getModContainer(MOD_ID).map(
                    container -> container.getMetadata().getVersion().getFriendlyString()
            )
            .orElse("unknown");
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RegistryKey<DamageType> COMBAT_LOG_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, of("combat_log"));
    public static final Map<UUID, Integer> waitingForResponse = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> clientsWithMods = new ConcurrentHashMap<>();
    public static BeansUtilsServerConfig SERVER_CONFIG;

    @Override
    public void onInitialize() {
        registerPayloads();

//        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
//            return;
//        }

        SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(BeansUtilsServerConfig::new, RegisterType.BOTH);

        registerEvents();

        LOGGER.info("BeansUtils Common initialized ({})", MOD_VERSION);
    }

    public static Identifier of(String path) {
        return Identifier.of(MOD_ID, path);
    }

    private static void registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(EndTick.INSTANCE);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && MinecraftClient.getInstance().isInSingleplayer()) {
            return;
        } else {
//            ServerPlayConnectionEvents.JOIN.register(HandshakePayload::attemptHandshake);
            ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
                if (entity instanceof PlayerEntity) {
                    HandshakePayload.attemptHandshake((ServerPlayerEntity) entity);
                }
            });
        }
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(HandshakePayload.HandshakeC2SPayload.ID, HandshakePayload.HandshakeC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(HandshakePayload.HandshakeS2CPayload.ID, HandshakePayload.HandshakeS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CombatPayload.CombatS2CPayload.ID, CombatPayload.CombatS2CPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(HandshakePayload.HandshakeC2SPayload.ID, (BeansUtils::playerHandshakeRespond));
    }

    public static void handshakeServerTick(MinecraftServer server) {
        waitingForResponse.replaceAll((uuid, ticks) -> ticks - 1);
        waitingForResponse.entrySet().removeIf(entry -> {
            UUID uuid = entry.getKey();
            int ticksLeft = entry.getValue();
            if (ticksLeft <= 0) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                if (player != null) {
                    playerHandshakeTimeout(player);
                    return true;
                }
            }
            return false;
        });
    }

    private static final ClickEvent CLICK_EVENT = new ClickEvent.OpenUrl(URI.create("https://github.com/Destruction-Studios/beans-utils"));

    public static void playerHandshakeTimeout(ServerPlayerEntity player) {
        LOGGER.info("{} failed to respond to handshake", Objects.requireNonNull(player.getDisplayName()).getString());
        waitingForResponse.remove(player.getUuid());
        if (SERVER_CONFIG.requireMod) {
            player.networkHandler.disconnect(Text.literal(SERVER_CONFIG.noModDisconnectMessage).styled(style -> {
                        style.withColor(Formatting.AQUA);
                        style.withClickEvent(CLICK_EVENT);
                        return style;
                    })

            );
            return;
        } else {
            clientsWithMods.put(player.getUuid(), false);
        }
        if (SERVER_CONFIG.notifyPlayersWithNoMod) {
                player.sendMessage(Text.literal(SERVER_CONFIG.notifyPlayerMessage).styled(style -> {
                style.withColor(Formatting.GREEN);
                style.withUnderline(true);
                style.withClickEvent(CLICK_EVENT);
                return style;
            }));
        }
    }

    public static void playerHandshakeRespond(HandshakePayload.HandshakeC2SPayload payload, ServerPlayNetworking.Context context) {
        String receivedVersion = payload.modVersion();
        ServerPlayerEntity player = context.player();

        waitingForResponse.remove(player.getUuid());

        if (!Objects.equals(receivedVersion, BeansUtils.MOD_VERSION)) {
            LOGGER.info("{} has mismatched client version", Objects.requireNonNull(player.getDisplayName()).getString());
            String message = "Outdated/Incorrect BeansUtils mod version (Client Version: %s, Server Version: %s)".formatted(receivedVersion, BeansUtils.MOD_VERSION);
            player.networkHandler.disconnect(Text.of(message));
        } else {
            LOGGER.info("{} has successfully joined with matching BeansUtils version!!", Objects.requireNonNull(player.getDisplayName()).getString());

            clientsWithMods.put(player.getUuid(), true);
        }
    }

    public static boolean doesPlayerHaveMod(UUID uuid) {
        return clientsWithMods.getOrDefault(uuid, false);
    }

}