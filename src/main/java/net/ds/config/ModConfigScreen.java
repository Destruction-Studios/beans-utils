package net.ds.config;

import net.ds.BeansUtils;
import net.ds.compat.CompatManager;
import net.ds.compat.YACLConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;

import java.net.URI;

public class ModConfigScreen {
    public static Screen create(Screen parent) {
        if (!CompatManager.isYACLInstalled()) {
            BeansUtils.LOGGER.warn("YACL is not installed...");
            return new ConfirmScreen((result) -> {
                if (result) {
                    Util.getOperatingSystem().open(URI.create("https://modrinth.com/mod/yacl?loader=fabric&version=1.21.10#download"));
                }
                MinecraftClient.getInstance().setScreen(parent);
            }, Text.literal("YACL is not installed.").withColor(Colors.RED), Text.of("Would you like to install it?"), ScreenTexts.YES, ScreenTexts.NO);
        } else {
            BeansUtils.LOGGER.info("Found YACL!!");
            return YACLConfigScreen.createConfigScreen(parent);
        }
    }
}
