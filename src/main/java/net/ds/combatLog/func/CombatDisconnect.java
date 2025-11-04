package net.ds.combatLog.func;

import net.ds.BeansUtils;
import net.ds.combatLog.CombatData;
import net.ds.combatLog.IEntityDataSaver;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minidev.json.writer.BeansMapper;

import java.lang.ref.Reference;

public class CombatDisconnect {
    public static void OnPlayerDisconnect(ServerPlayerEntity playerEntity) {
        if (CombatData.isInCombat((IEntityDataSaver) playerEntity)) {
            if (!BeansUtils.SERVER_CONFIG.combatLogging.killPlayerUponCombatLogging) {
                return;
            }

            CombatData.endCombat((IEntityDataSaver) playerEntity);

            DamageSource damageSource = new DamageSource(
                    playerEntity.getRegistryManager()
                            .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                            .getEntry(BeansUtils.COMBAT_LOG_DAMAGE.getValue()).get()
            );
            playerEntity.damage(playerEntity.getEntityWorld(), damageSource, 100000000000.0f);
        }
    }
}
