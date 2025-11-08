package net.ds;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.ds.config.BeansUtilsClientConfig;
import net.ds.network.CombatPayload;
import net.ds.network.HandshakePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class BeansUtilsClient implements ClientModInitializer {
    public static boolean isInCombat = false;
    public static boolean receivedHandshake = false;

    public static BeansUtilsClientConfig CLIENT_CONFIG = ConfigApiJava.registerAndLoadConfig(BeansUtilsClientConfig::new, RegisterType.CLIENT);

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.HandshakeS2CPayload.ID, ((handshakeS2CPayload, context) -> {
            BeansUtils.LOGGER.info("Received Server Payload");
            if (CLIENT_CONFIG.debug.rejectHandshake) {
                BeansUtils.LOGGER.info("Rejecting Handshake");
                return;
            }
            receivedHandshake = true;
            HandshakePayload.returnHandshake();
        }));
        ClientPlayNetworking.registerGlobalReceiver(CombatPayload.CombatS2CPayload.ID, ((combatS2CPayload, context) -> {
            isInCombat = combatS2CPayload.isInCombat();
            BeansUtils.LOGGER.info("Received combat packet: {}", isInCombat);
        }));

        BeansUtils.LOGGER.info("BeansUtils Client initialized ({})", BeansUtils.MOD_VERSION);
    }
}
