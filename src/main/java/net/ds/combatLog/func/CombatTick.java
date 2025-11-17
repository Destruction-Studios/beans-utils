package net.ds.combatLog.func;

import net.ds.combatLog.CombatData;
import net.ds.interfaces.IEntityDataSaver;
import net.ds.network.CombatPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class CombatTick {
    public static void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            IEntityDataSaver data = (IEntityDataSaver) player;

            if (!CombatData.isInCombat(data)) continue;

            int combatTime = CombatData.getCombatTime(data);

            if (combatTime > 0) {
                CombatData.decreaseCombatTime(data);
                SendCombatMessage.SendInCombatMessage(player, combatTime);
            } else if (CombatData.isInCombat(data)) {
                CombatData.endCombat(data);
                SendCombatMessage.SendLeaveCombatMessage(player);
                CombatPayload.sendLeaveCombat(player);
            }
        }
    }
}
