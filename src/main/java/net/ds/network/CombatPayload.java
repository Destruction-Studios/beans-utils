package net.ds.network;

import net.ds.BeansUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CombatPayload {
    public record CombatS2CPayload(Boolean isInCombat) implements CustomPayload {
        public static final Identifier COMBAT_PAYLOAD_ID = BeansUtils.of("combats2c");
        public static final CustomPayload.Id<CombatS2CPayload> ID = new CustomPayload.Id<>(COMBAT_PAYLOAD_ID);
        public static final PacketCodec<RegistryByteBuf, CombatS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, CombatS2CPayload::isInCombat, CombatS2CPayload::new);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void sendEnterCombat(ServerPlayerEntity player) {
        if (BeansUtils.doesPlayerHaveMod(player.getUuid())) {
            CombatS2CPayload payload = new CombatS2CPayload(true);

            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendLeaveCombat(ServerPlayerEntity player) {
        if (BeansUtils.doesPlayerHaveMod(player.getUuid())) {
            CombatS2CPayload payload = new CombatS2CPayload(false);

            ServerPlayNetworking.send(player, payload);
        }
    }
}
