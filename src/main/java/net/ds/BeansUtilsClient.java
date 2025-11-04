package net.ds;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.ds.config.BeansUtilsClientConfig;
import net.ds.network.Handshake;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class BeansUtilsClient implements ClientModInitializer {
    public static BeansUtilsClientConfig CLIENT_CONFIG = ConfigApiJava.registerAndLoadConfig(BeansUtilsClientConfig::new, RegisterType.CLIENT);

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Handshake.HandshakeS2CPayload.ID, ((handshakeS2CPayload, context) -> {
            BeansUtils.LOGGER.info("RECIVED PAYLOAD!!!!!!!");
        }));

        BeansUtils.LOGGER.info("Client Initialized");
    }
}
