package net.ds.network;

import net.ds.BeansUtils;
import net.ds.config.ModServerConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class HandshakePayload {
    private static final int HANDSHAKE_TIMEOUT_TICKS = 60;
    public record HandshakeS2CPayload(String modVersion) implements CustomPayload {
        public static final Identifier HANDSHAKE_PAYLOAD_ID = BeansUtils.of("handshakes2c");
        public static final CustomPayload.Id<HandshakeS2CPayload> ID = new CustomPayload.Id<>(HANDSHAKE_PAYLOAD_ID);
        public static final PacketCodec<RegistryByteBuf, HandshakeS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, HandshakeS2CPayload::modVersion, HandshakeS2CPayload::new);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record HandshakeC2SPayload(String modVersion) implements CustomPayload {
        public static final Identifier HANDSHAKE_PAYLOAD_ID = BeansUtils.of("handshakec2s");
        public static final CustomPayload.Id<HandshakeC2SPayload> ID = new CustomPayload.Id<>(HANDSHAKE_PAYLOAD_ID);
        public static final PacketCodec<RegistryByteBuf, HandshakeC2SPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, HandshakeC2SPayload::modVersion, HandshakeC2SPayload::new);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void attemptHandshake(ServerPlayerEntity player) {
        HandshakeS2CPayload payload = new HandshakeS2CPayload(BeansUtils.MOD_VERSION);

        BeansUtils.LOGGER.info("Requesting Handshake from {}...", Objects.requireNonNull(player.getDisplayName()).getString());

        ServerPlayNetworking.send(player, payload);
        BeansUtils.waitingForResponse.put(player.getUuid(), ModServerConfig.INSTANCE.getHandshakeTimeout());
//        ServerTick.INSTANCE.addDelayedRunnable(new DelayedRunnable(ModServerConfig.INSTANCE.getHandshakeTimeout(), () -> {
//
//        }));
    }

    public static void returnHandshake() {
        HandshakeC2SPayload payload = new HandshakeC2SPayload(BeansUtils.MOD_VERSION);

        BeansUtils.LOGGER.info("Returning Payload with mod version {}", BeansUtils.MOD_VERSION);

        ClientPlayNetworking.send(payload);
    }
}
