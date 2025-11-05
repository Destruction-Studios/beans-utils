package net.ds.mixin;

import net.ds.BeansUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderEyeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public class EyeOfEnderMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void injectUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (BeansUtils.SERVER_CONFIG.featureToggling.eyesOfEnderDisabled) {
            cir.setReturnValue(ActionResult.PASS);
            cir.cancel();
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void injectUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (BeansUtils.SERVER_CONFIG.featureToggling.eyesOfEnderDisabled) {
            cir.setReturnValue(ActionResult.PASS);
            cir.cancel();
        }
    }
}
