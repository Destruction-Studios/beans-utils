package net.ds.mixin.item;

import net.ds.combatLog.CombatData;
import net.ds.config.ModServerConfig;
import net.ds.interfaces.IEntityDataSaver;
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
        if (ModServerConfig.INSTANCE.getDisabledEnderPearls()
                && CombatData.isInCombat((IEntityDataSaver) user)) {
            cir.setReturnValue(ActionResult.PASS);
            cir.cancel();
        }
    }
}
