package net.ds.combatLog.func;

import net.ds.combatLog.CombatData;
import net.ds.combatLog.IEntityDataSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

public class CombatTick {
    public static void CombatTick(MinecraftServer server) {
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
            IEntityDataSaver data = (IEntityDataSaver) player;

            if (!CombatData.isInCombat(data)) continue;

            int combatTime = CombatData.getCombatTime(data);

            if (combatTime > 0) {
                CombatData.decreaseCombatTime(data);
                SendCombatMessage.SendInCombatMessage(player, combatTime);
            } else if (CombatData.isInCombat(data)) {
                CombatData.endCombat(data);
                SendCombatMessage.SendLeaveCombatMessage(player);
            }
        }
    }
}
