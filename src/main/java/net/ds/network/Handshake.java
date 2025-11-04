package net.ds.network;

import com.mojang.serialization.Codec;
import net.ds.BeansUtils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;

public class Handshake {
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

    public static void attemptHandshake(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.getPlayer();
        HandshakeS2CPayload payload = new HandshakeS2CPayload(BeansUtils.MOD_VERSION);

        ServerPlayNetworking.send(player, payload);

        BeansUtils.LOGGER.info("SENT PAYLOAD!!");
    }
}
