package net.ds;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.ds.config.BeansUtilsServerConfig;
import net.ds.events.EndTick;
import net.ds.network.CombatPayload;
import net.ds.network.HandshakePayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BeansUtils implements ModInitializer {
	public static final String MOD_ID = "beans-utils";
	public static final String MOD_VERSION = "1.0.0";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static BeansUtilsServerConfig SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(BeansUtilsServerConfig::new, RegisterType.BOTH);

	public static final RegistryKey<DamageType> COMBAT_LOG_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, of("combat_log"));

	private static final Map<UUID, Boolean> CLIENTS_WITH_MODS = new ConcurrentHashMap<>();

	public static Identifier of(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		registerPayloads();
		registerEvents();

		LOGGER.info("BeansUtils Common initialized ({})", MOD_VERSION);
	}

	private static void registerPayloads() {
		PayloadTypeRegistry.playC2S().register(HandshakePayload.HandshakeC2SPayload.ID, HandshakePayload.HandshakeC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(HandshakePayload.HandshakeS2CPayload.ID, HandshakePayload.HandshakeS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(CombatPayload.CombatS2CPayload.ID, CombatPayload.CombatS2CPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(HandshakePayload.HandshakeC2SPayload.ID, ((handshakeC2SPayload, context) -> {
			String receivedVersion = handshakeC2SPayload.modVersion();
			ServerPlayerEntity player = context.player();

			if (!Objects.equals(receivedVersion, BeansUtils.MOD_VERSION)) {
				LOGGER.info("{} has mismatched client version", Objects.requireNonNull(player.getDisplayName()).getString());
				String message = "Outdated/Incorrect BeansUtils mod version (Client Version: %s, Server Version: %s)".formatted(receivedVersion, BeansUtils.MOD_VERSION);
				player.networkHandler.disconnect(Text.of(message));
			} else {
				LOGGER.info("{} has successfully joined with matching BeansUtils version!!", Objects.requireNonNull(player.getDisplayName()).getString());

				CLIENTS_WITH_MODS.put(player.getUuid(), true);
			}
		}));
	}

	private static void registerEvents() {
		ServerTickEvents.END_SERVER_TICK.register(EndTick.INSTANCE);
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && MinecraftClient.getInstance().isInSingleplayer()) {
			return;
		} else {
			ServerPlayConnectionEvents.JOIN.register(HandshakePayload::attemptHandshake);
		}
	}

	public static boolean doesPlayerHaveMod(UUID uuid) {
        return CLIENTS_WITH_MODS.getOrDefault(uuid, false);
	}
}