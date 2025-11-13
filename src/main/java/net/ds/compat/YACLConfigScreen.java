package net.ds.compat;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.ds.config.ModClientConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class YACLConfigScreen {
    public static Screen createConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(txt("Beans Utils Config"))
                .category(ConfigCategory.createBuilder()
                        .name(txt("Client"))
                        .group(OptionGroup.createBuilder()
                                .name(txt("Server"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(txt("Ignore Reload"))
                                        .description(OptionDescription.of(txt("Ignores the resource pack reload if the client already has a resource pack of the same hash installed.")))
                                        .binding(ModClientConfig.DEFAULTS.getDontReloadResources(), () -> ModClientConfig.INSTANCE.getDontReloadResources(), v -> ModClientConfig.INSTANCE.setDontReloadResources(v))
                                        .controller(YACLConfigScreen::createEnableDisableCheckbox)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(txt("Combat Tagging"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(txt("Prevent Leaving"))
                                        .description(OptionDescription.of(txt("Prevents leaving the server while in combat.")))
                                        .binding(ModClientConfig.DEFAULTS.getPreventLeaving(), () -> ModClientConfig.INSTANCE.getPreventLeaving(), v -> ModClientConfig.INSTANCE.setPreventLeaving(v))
                                        .controller(YACLConfigScreen::createEnableDisableCheckbox)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(txt("Debug"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(txt("Ignore Handshake"))
                                        .description(OptionDescription.of(txt("Ignores the server handshake, acting as if the client does not have the mod.")))
                                        .binding(ModClientConfig.DEFAULTS.getIgnoreHandshake(), () -> ModClientConfig.INSTANCE.getIgnoreHandshake(), v -> ModClientConfig.INSTANCE.setIgnoreHandshake(v))
                                        .controller(YACLConfigScreen::createEnableDisableCheckbox)
                                        .build())
                                .build())
                        .build()).save(() -> {
                            ModClientConfig.INSTANCE.save();
                }).build().generateScreen(parent);
    }

    private static BooleanControllerBuilder createEnableDisableCheckbox(Option<Boolean> opt) {
        return BooleanControllerBuilder.create(opt)
                .formatValue(state -> state ? txt("Enabled") : txt("Disabled"))
                .coloured(true);
    }

    private static Text txt(String string) {
        return Text.of(string);
    }
}
