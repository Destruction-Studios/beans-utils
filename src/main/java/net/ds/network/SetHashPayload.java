package net.ds.network;

import net.ds.BeansUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class SetHashPayload {
    public record SetHashC2SPayload() implements CustomPayload {
        public static final Identifier HANDSHAKE_PAYLOAD_ID = BeansUtils.of("sethashc2s");
        public static final CustomPayload.Id<SetHashC2SPayload> ID = new CustomPayload.Id<>(HANDSHAKE_PAYLOAD_ID);
        public static final PacketCodec<RegistryByteBuf, SetHashC2SPayload> CODEC = PacketCodec.of(
                ((value, buf) -> {
                }),
                buf -> new SetHashC2SPayload()
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void requestHashReset() {
        ClientPlayNetworking.send(new SetHashC2SPayload());
    }
}
