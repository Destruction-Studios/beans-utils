package net.ds.mixin;

import net.ds.combatLog.func.CombatDisconnect;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void injectDisconnect(CallbackInfo ci) {
        CombatDisconnect.OnPlayerDisconnect((ServerPlayerEntity) (Object) this);
    }
}
