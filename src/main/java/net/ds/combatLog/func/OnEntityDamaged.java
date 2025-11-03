package net.ds.combatLog.func;

import net.ds.BeansUtils;
import net.ds.combatLog.CombatData;
import net.ds.combatLog.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class OnEntityDamaged {
    public static void CheckCombat(Entity entity) {
        if (!BeansUtils.SERVER_CONFIG.combatLogging.combatTaggingEnabled) {
            return;
        }
        LivingEntity target = (LivingEntity) entity;
        LivingEntity attacker = target.getAttacker();

        if (attacker == null) return;

        if (target instanceof PlayerEntity) {
            Identifier attackerIdentifier = Registries.ENTITY_TYPE.getId(attacker.getType());
            if (!BeansUtils.SERVER_CONFIG.combatLogging.combatTriggeringEntities.containsKey(attackerIdentifier)) {
                return;
            }
            CombatData.startCombat((IEntityDataSaver) target);
            if (attacker instanceof PlayerEntity) {
                CombatData.startCombat((IEntityDataSaver) attacker);
            }
        }
    }
}
