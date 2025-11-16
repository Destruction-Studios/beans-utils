package net.ds.combatLog.func;

import net.ds.combatLog.CombatData;
import net.ds.config.ModServerConfig;
import net.ds.interfaces.IEntityDataSaver;
import net.ds.network.CombatPayload;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnEntityDamaged {
    public static void CheckCombat(Entity entity) {
        if (!ModServerConfig.INSTANCE.getCombatTaggingEnabled()) {
            return;
        }
        LivingEntity target = (LivingEntity) entity;
        LivingEntity attacker = target.getAttacker();

        if (attacker == null) return;

        if (target instanceof PlayerEntity) {
            String attackerIdentifier = Registries.ENTITY_TYPE.getId(attacker.getType()).toString();
            if (!ModServerConfig.INSTANCE.getCombatTriggeringEntities().contains(attackerIdentifier)) {
                return;
            }
            CombatData.startCombat((IEntityDataSaver) target);
            CombatPayload.sendEnterCombat((ServerPlayerEntity) target);

            if (attacker instanceof PlayerEntity) {
                CombatData.startCombat((IEntityDataSaver) attacker);
                CombatPayload.sendEnterCombat((ServerPlayerEntity) attacker);
            }
        }
    }
}
