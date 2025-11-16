package net.ds.mixin.client;

import net.ds.BeansUtilsClient;
import net.ds.config.ModClientConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class LeaveButtonMixin {
    @Shadow @Nullable private ButtonWidget exitButton;

    @Inject(method = "render", at = @At("TAIL"))
    public void renderMixin(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (this.exitButton != null && ModClientConfig.INSTANCE.getPreventLeaving()) {
            this.exitButton.active = !BeansUtilsClient.isInCombat;
        }
    }
}
