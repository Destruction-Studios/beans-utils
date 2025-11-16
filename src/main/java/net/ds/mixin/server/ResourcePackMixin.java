package net.ds.mixin.server;

import net.ds.BeansUtils;
import net.ds.util.Util;
import net.ds.config.ModServerConfig;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ResourcePackSendS2CPacket.class)
public class ResourcePackMixin {
    @Inject(method = "hash()Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    public void getHash(CallbackInfoReturnable<String> cir) {
        if (!Util.isResourcePackUrlOverrideSet()) {
            BeansUtils.LOGGER.info("No custom resource pack.");
            return;
        }

        if (!Objects.equals(ModServerConfig.INSTANCE.getCustomHash(), "")) {
            BeansUtils.LOGGER.info("Custom hash found: {}", ModServerConfig.INSTANCE.getCustomHash());
            cir.setReturnValue(ModServerConfig.INSTANCE.getCustomHash());
        } else  {
            BeansUtils.LOGGER.warn("No hash for custom resource pack.");
        }
    }

    @Inject(at= @At("HEAD"), method = "Lnet/minecraft/network/packet/s2c/common/ResourcePackSendS2CPacket;url()Ljava/lang/String;", cancellable = true)
    public void getResourceURL(CallbackInfoReturnable<String> cir) {
        if (Util.isResourcePackUrlOverrideSet()) {
            BeansUtils.LOGGER.info("Custom resource pack found");
            cir.setReturnValue(ModServerConfig.INSTANCE.getCustomResourcePackURL());
        }
    }
}
