package net.ds;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.ds.carpet.BeansUtilsExtension;
import net.ds.config.BeansUtilsClientConfig;
import net.ds.config.BeansUtilsServerConfig;
import net.ds.events.EndTick;
import net.ds.network.Handshake;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BeansUtils implements ModInitializer {
	public static final String MOD_ID = "beans-utils";
	public static final String MOD_VERSION = "1.0.0";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final CarpetExtension CARPET_EXTENSION = new BeansUtilsExtension();
	public static final Map<UUID, Boolean> CLIENTS_WITH_MODS = new ConcurrentHashMap<>();
	public static BeansUtilsServerConfig SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(BeansUtilsServerConfig::new, RegisterType.BOTH);

	@Override
	public void onInitialize() {
		CarpetServer.manageExtension(CARPET_EXTENSION);

		registerPayloads();
		registerEvents();

		LOGGER.info("BeansUtils initialized");
	}

	private static void registerPayloads() {
		PayloadTypeRegistry.playC2S().register(Handshake.HandshakeC2SPayload.ID, Handshake.HandshakeC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(Handshake.HandshakeS2CPayload.ID, Handshake.HandshakeS2CPayload.CODEC);
	}

	private static void registerEvents() {
		ServerTickEvents.END_SERVER_TICK.register(EndTick.INSTANCE);
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && MinecraftClient.getInstance().isInSingleplayer()) {
			return;
		} else {
			ServerPlayConnectionEvents.JOIN.register(Handshake::attemptHandshake);
		}
	}

	public static Identifier of(String path) {
		return Identifier.of(path);
	}

	public static final RegistryKey<DamageType> COMBAT_LOG_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, of("combat_log"));
}