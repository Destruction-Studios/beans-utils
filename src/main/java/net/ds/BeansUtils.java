package net.ds;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.ds.carpet.BeansUtilsExtension;
import net.ds.config.BeansUtilsClientConfig;
import net.ds.config.BeansUtilsServerConfig;
import net.ds.events.EndTick;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeansUtils implements ModInitializer {
	public static final String MOD_ID = "beans-utils";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final CarpetExtension CARPET_EXTENSION = new BeansUtilsExtension();
	public static BeansUtilsClientConfig CONFIG = ConfigApiJava.registerAndLoadConfig(BeansUtilsClientConfig::new, RegisterType.CLIENT);
	public static BeansUtilsServerConfig SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(BeansUtilsServerConfig::new, RegisterType.BOTH);

	@Override
	public void onInitialize() {
		CarpetServer.manageExtension(CARPET_EXTENSION);

		ServerTickEvents.END_SERVER_TICK.register(EndTick.INSTANCE);

		LOGGER.info("BeansUtils initialized");
	}

	public static final RegistryKey<DamageType> COMBAT_LOG_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(BeansUtils.MOD_ID, "combat_log"));
}