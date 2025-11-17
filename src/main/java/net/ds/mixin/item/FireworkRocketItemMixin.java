package net.ds.mixin.item;

import net.ds.combatLog.CombatData;
import net.ds.config.ModServerConfig;
import net.ds.interfaces.IPlayerDataSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public class FireworkRocketItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void injectUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (CombatData.isInCombat((IPlayerDataSaver) user) && ModServerConfig.INSTANCE.getDisableFireworkRockets()) {
            cir.setReturnValue(ActionResult.PASS);
            cir.cancel();
        }
    }
}
