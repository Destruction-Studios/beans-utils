package net.ds.mixin.items;

import net.ds.BeansUtils;
import net.ds.combatLog.CombatData;
import net.ds.combatLog.IEntityDataSaver;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public class TridentItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void injectUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (EnchantmentHelper.getTridentSpinAttackStrength(user.getStackInHand(hand), user) > 0.0f &&
                BeansUtils.SERVER_CONFIG.combatDisabledFeatures.disableTridents && CombatData.isInCombat((IEntityDataSaver) user)) {
            cir.setReturnValue(ActionResult.PASS);
            cir.cancel();
        }
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"), cancellable = true)
    public void injectOnStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfoReturnable<Boolean> cir) {
        if (EnchantmentHelper.getTridentSpinAttackStrength(stack, user) > 0.0f &&
                BeansUtils.SERVER_CONFIG.combatDisabledFeatures.disableTridents && CombatData.isInCombat((IEntityDataSaver) user)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
