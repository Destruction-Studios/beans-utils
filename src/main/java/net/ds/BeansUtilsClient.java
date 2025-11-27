package net.ds;

import net.ds.config.ModClientConfig;
import net.ds.config.sync.MutableServerConfig;
import net.ds.network.CombatPayload;
import net.ds.network.HandshakePayload;
import net.ds.network.ConfigSyncPayload;
import net.ds.util.ToastUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BeansUtilsClient implements ClientModInitializer {
    public static boolean isInCombat = false;
    public static boolean receivedHandshake = false;
    public static MutableServerConfig SERVER_CONFIG = new MutableServerConfig();

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.HandshakeS2CPayload.ID, ((handshakeS2CPayload, context) -> {
            BeansUtils.LOGGER.info("Received Server Payload");
            if (ModClientConfig.INSTANCE.getIgnoreHandshake()) {
                BeansUtils.LOGGER.info("Rejecting Handshake");
                return;
            }
            receivedHandshake = true;
            HandshakePayload.returnHandshake();

            if (ModClientConfig.INSTANCE.getSendServerConnectToast()) {
                ToastUtil.toasty(Text.of("Connected to BeansUtils server!!"));
            }
        }));
        ClientPlayNetworking.registerGlobalReceiver(CombatPayload.CombatS2CPayload.ID, ((combatS2CPayload, context) -> {
            isInCombat = combatS2CPayload.isInCombat();
            BeansUtils.LOGGER.info("Received combat packet: {}", isInCombat);
        }));
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.ServerConfigS2CPayload.ID, (packet, context) -> {
            BeansUtils.LOGGER.info("Received server config...");

            if (SERVER_CONFIG.isPresent()) {
                ToastUtil.toasty(Text.literal("Server config has been updated!").formatted(Formatting.YELLOW));
            }

            SERVER_CONFIG.onReceivedServerConfig(packet.serverConfig());
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (receivedHandshake) {
                receivedHandshake = false;
                isInCombat = false;
                SERVER_CONFIG.clearServerConfig();
                BeansUtils.LOGGER.info("Disconnected from server");
            }

        });

        BeansUtils.LOGGER.info("BeansUtils Client initialized ({})", BeansUtils.MOD_VERSION);
    }
}
