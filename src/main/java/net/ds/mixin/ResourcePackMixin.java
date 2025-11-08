package net.ds.mixin;

import net.ds.BeansUtils;
import net.ds.Utils;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Mixin(ResourcePackSendS2CPacket.class)
public class ResourcePackMixin {
    @Inject(method = "hash()Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    public void getHash(CallbackInfoReturnable<String> cir) {
        if (!Utils.isResourcePackUrlOverrideSet()) {
            BeansUtils.LOGGER.info("No custom resource pack.");
            return;
        }
        File hashFile = Utils.getOrCreateHashFile();

        try {
            Scanner fileReader = new Scanner(hashFile);
            if (fileReader.hasNextLine()) {
                String hashFromFile = fileReader.nextLine();
                BeansUtils.LOGGER.info("Custom hash found: {}", hashFromFile);
                cir.setReturnValue(hashFromFile);
            } else {
                BeansUtils.LOGGER.warn("No hash for custom resource pack.");
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            BeansUtils.LOGGER.error("Could not get hash file: {}", String.valueOf(e));
        }
    }

    @Inject(at= @At("HEAD"), method = "Lnet/minecraft/network/packet/s2c/common/ResourcePackSendS2CPacket;url()Ljava/lang/String;", cancellable = true)
    public void getResourceURL(CallbackInfoReturnable<String> cir) {
        if (Utils.isResourcePackUrlOverrideSet()) {
            BeansUtils.LOGGER.info("Custom resource pack found");
            cir.setReturnValue(BeansUtils.SERVER_CONFIG.resourcePackSettings.serverResourcePackURL);
        }
    }
}
