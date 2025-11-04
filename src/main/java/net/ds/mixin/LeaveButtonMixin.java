package net.ds.mixin;

import net.ds.BeansUtilsClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GameMenuScreen.class)
public class LeaveButtonMixin {
    @Shadow @Nullable private ButtonWidget exitButton;

    @Inject(method = "render", at = @At("TAIL"))
    public void renderMixin(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (this.exitButton != null) {
            this.exitButton.active = !BeansUtilsClient.CLIENT_CONFIG.preventLeavingWhenInCombat || !BeansUtilsClient.isInCombat;
        }
    }
}
