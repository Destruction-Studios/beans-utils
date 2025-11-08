package net.ds.mixin.client;

import net.ds.BeansUtils;
import net.ds.BeansUtilsClient;
import net.ds.Utils;
import net.ds.interfaces.FilePackResource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.Collection;

@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {
    @Shadow
    @Final
    protected ClientConnection connection;

    @Inject(method = "onResourcePackSend", at = @At("HEAD"), cancellable = true)
    private void onResourcePackSendInject(ResourcePackSendS2CPacket packet, CallbackInfo ci) {
        if (!BeansUtilsClient.CLIENT_CONFIG.noResourcePackReload) {
            return;
        }
        if (packet.hash().isBlank()) {
            return;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Collection<ResourcePackProfile> packs = minecraftClient.getResourcePackManager().getEnabledProfiles();
        for (ResourcePackProfile packProfile : packs) {
           ResourcePack resourcePack = packProfile.createResourcePack();
           if (resourcePack instanceof FilePackResource packResource) {
               File file = packResource.noload$getFile();
               if (file == null) {
                   continue;
               }
               String packSha1 = Utils.getSha1FromFile(file);
               if(packSha1.equals(packet.hash())) {
                   BeansUtils.LOGGER.info("Ignoring server resource pack.");
                   sendSuccessful(packet);
                   ci.cancel();
                   return;
               }
           }
        }
    }

    @Unique
    private void sendSuccessful(ResourcePackSendS2CPacket packet) {
        connection.send(new ResourcePackStatusC2SPacket(packet.id(), ResourcePackStatusC2SPacket.Status.ACCEPTED));
        connection.send(new ResourcePackStatusC2SPacket(packet.id(), ResourcePackStatusC2SPacket.Status.DOWNLOADED));
        connection.send(new ResourcePackStatusC2SPacket(packet.id(), ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
    }
}
