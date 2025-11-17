package net.ds.combatLog.func;

import net.ds.BeansUtils;
import net.ds.combatLog.CombatData;
import net.ds.config.ModServerConfig;
import net.ds.interfaces.IPlayerDataSaver;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;

public class CombatDisconnect {
    public static void OnPlayerDisconnect(ServerPlayerEntity playerEntity) {
        if (CombatData.isInCombat((IPlayerDataSaver) playerEntity)) {
            if (!ModServerConfig.INSTANCE.getKillPlayerUponCombatLogging()) {
                return;
            }

            CombatData.endCombat((IPlayerDataSaver) playerEntity);

            DamageSource damageSource = new DamageSource(
                    playerEntity.getRegistryManager()
                            .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                            .getEntry(BeansUtils.COMBAT_LOG_DAMAGE.getValue()).get()
            );
            playerEntity.damage(playerEntity.getEntityWorld(), damageSource, 100000000000.0f);
        }
    }
}
