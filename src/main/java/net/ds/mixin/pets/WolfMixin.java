package net.ds.mixin.pets;

import net.ds.BeansUtils;
import net.ds.interfaces.IPetDataSaver;
import net.ds.petRespawning.PetManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public abstract class WolfMixin implements IPetDataSaver {
    @Unique
    private boolean canRespawn = false;

    @Override
    public boolean beans_utils$getCanRespawn() {
        return canRespawn;
    }

    @Override
    public void beans_utils$setCanRespawn(boolean canRespawn) {
        this.canRespawn = canRespawn;
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        WolfEntity wolf = (WolfEntity) (Object) this;
        if (!wolf.isTamed() || canRespawn || !wolf.isOwner(player)) {
            return;
        }
        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isOf(Items.GOLDEN_APPLE)) {
            return;
        }
        canRespawn = true;
        PetManager.playSetRespawnEffect(wolf);
        stack.decrementUnlessCreative(1, player);
        cir.setReturnValue(ActionResult.SUCCESS);
        cir.cancel();
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void injectOnDeath(DamageSource damageSource, CallbackInfo ci) {
        BeansUtils.LOGGER.info("WOLF DIED :(");
        WolfEntity wolf = (WolfEntity) (Object) this;
        if (!wolf.isTamed()) {
            BeansUtils.LOGGER.info("Not tamed...");
            return;
        }
        if (!canRespawn) {
            BeansUtils.LOGGER.info("Cant respawn :(");
            return;
        }
        LivingEntity player = wolf.getOwner();
        assert player != null;
        BeansUtils.LOGGER.info("OWNER: {}", player.getName().getString());
    }

    @Inject(method = "writeCustomData", at = @At("HEAD"))
    public void injectWriteData(WriteView view, CallbackInfo ci) {
        view.putBoolean("canRespawn", canRespawn);
    }

    @Inject(method = "readCustomData", at =@At("HEAD"))
    public void injectReadData(ReadView view, CallbackInfo ci) {
        this.canRespawn = view.getBoolean("canRespawn", false);
    }
}
