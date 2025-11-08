package net.ds.mixin.items;

import net.ds.BeansUtils;
import net.ds.combatLog.CombatData;
import net.ds.combatLog.IEntityDataSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderPearlItem.class)
public class EnderPearlMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void injectUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (BeansUtils.SERVER_CONFIG.combatDisabledFeatures.disableEnderPearls
                && CombatData.isInCombat((IEntityDataSaver) user)) {
            cir.setReturnValue(ActionResult.PASS);
            cir.cancel();
        }
    }
}
