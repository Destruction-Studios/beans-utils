package net.ds.network;

import net.ds.BeansUtils;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ServerConfigPayload {
    public record ServerConfigS2CPayload(byte[] serverConfig) implements CustomPayload {
        public static final Identifier CONFIG_SERVER_ID = BeansUtils.of("server_config_s2c");
        public static final CustomPayload.Id<ServerConfigS2CPayload> ID = new CustomPayload.Id<>(CONFIG_SERVER_ID);
        public static final PacketCodec<RegistryByteBuf, ServerConfigS2CPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.BYTE_ARRAY, ServerConfigS2CPayload::serverConfig,
                ServerConfigS2CPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {return ID;}
    }
}
