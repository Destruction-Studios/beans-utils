package net.ds;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.ds.config.BeansUtilsServerConfig;
import net.ds.events.EndTick;
import net.ds.events.ServerStopping;
import net.ds.network.CombatPayload;
import net.ds.network.HandshakePayload;
import net.ds.network.SetHashPayload;
import net.ds.petRespawning.func.GoldenAppleUsed;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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
    public static final BeansUtilsServerConfig SERVER_CONFIG =ConfigApiJava.registerAndLoadConfig(BeansUtilsServerConfig::new, RegisterType.BOTH);
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        registerPayloads();
        registerEvents();

//        CommandRegistrationCallback.EVENT.register(BeansUtilsCommands::registerCommands);

        Utils.getOrCreateHashFile();

        LOGGER.info("BeansUtils Common initialized ({})", MOD_VERSION);
    }

    public static Identifier of(String path) {
        return Identifier.of(MOD_ID, path);
    }

    private static void registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(EndTick.INSTANCE);
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerStopping.INSTANCE);
        UseEntityCallback.EVENT.register(GoldenAppleUsed::onGoldenAppleUsed);
        ServerLifecycleEvents.SERVER_STARTING.register((minecraftServer -> {
            SERVER = minecraftServer;
        }));
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && MinecraftClient.getInstance().isInSingleplayer()) {
            return;
        } else {
            ServerPlayerEvents.JOIN.register(HandshakePayload::attemptHandshake);
        }
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(HandshakePayload.HandshakeC2SPayload.ID, HandshakePayload.HandshakeC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(HandshakePayload.HandshakeS2CPayload.ID, HandshakePayload.HandshakeS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CombatPayload.CombatS2CPayload.ID, CombatPayload.CombatS2CPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SetHashPayload.SetHashC2SPayload.ID, SetHashPayload.SetHashC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(HandshakePayload.HandshakeC2SPayload.ID, (BeansUtils::playerHandshakeRespond));
        ServerPlayNetworking.registerGlobalReceiver(SetHashPayload.SetHashC2SPayload.ID, (BeansUtils::setHashC2S));
    }

    public static void setHashC2S(SetHashPayload.SetHashC2SPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if (!player.hasPermissionLevel(4)) {
            return;
        }
            try {
                if (Objects.equals(SERVER_CONFIG.resourcePackSettings.serverResourcePackURL, "")) {
                    BeansUtils.LOGGER.warn("No custom url set");
                    player.sendMessage(Text.literal("No custom url set").withColor(Colors.RED));
                    return;
                }
                String url = SERVER_CONFIG.resourcePackSettings.serverResourcePackURL;
                player.sendMessage(Text.literal("Downloading: " + url).withColor(Colors.BLUE));
                BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest);
                while (digestInputStream.read() != -1) {
                }
                digestInputStream.close();
                BeansUtils.LOGGER.info("D");

                BeansUtils.LOGGER.info("Resource pack downloaded, calculating hash");
                byte[] hash = digest.digest();
                StringBuilder hexString = new StringBuilder();

                for (byte b : hash) {
                    hexString.append(String.format("%02x", b));
                }

                String hashFinal = hexString.toString().toLowerCase(Locale.ROOT);
                File hashFile = Utils.getOrCreateHashFile();

                FileWriter writer = new FileWriter(hashFile);
                writer.write(hashFinal);
                writer.close();

                player.sendMessage(Text.literal("Successfully fetched hash: " + hashFinal).withColor(Colors.GREEN));
            } catch (NoSuchElementException e) {
                BeansUtils.LOGGER.error("Could not get resource pack url: {}", String.valueOf(e));
            } catch (MalformedURLException e) {
                BeansUtils.LOGGER.error("Invalid resource pack url: {}", String.valueOf(e));
            } catch (IOException e) {
                BeansUtils.LOGGER.error("IOException: {}", String.valueOf(e));
            } catch (NoSuchAlgorithmException e) {
                BeansUtils.LOGGER.error("Invalid alg: {}", String.valueOf(e));
            }
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