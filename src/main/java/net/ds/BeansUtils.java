package net.ds;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.ds.carpet.BeansUtilsExtension;
import net.ds.config.BeansUtilsClientConfig;
import net.ds.config.BeansUtilsServerConfig;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeansUtils implements ModInitializer {
	public static final String MOD_ID = "beans-utils";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final CarpetExtension CARPET_EXTENSION = new BeansUtilsExtension();
	public static BeansUtilsClientConfig CONFIG = ConfigApiJava.registerAndLoadConfig(BeansUtilsClientConfig::new, RegisterType.CLIENT);
	public static BeansUtilsServerConfig SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(BeansUtilsServerConfig::new, RegisterType.SERVER);

	@Override
	public void onInitialize() {
		CarpetServer.manageExtension(CARPET_EXTENSION);

		LOGGER.info("BeansUtils initialized");
	}

}