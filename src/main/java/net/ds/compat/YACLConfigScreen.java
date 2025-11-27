package net.ds.compat;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.impl.controller.StringControllerBuilderImpl;
import net.ds.BeansUtils;
import net.ds.BeansUtilsClient;
import net.ds.config.ModClientConfig;
import net.ds.config.sync.MutableServerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class YACLConfigScreen {
    public static Screen createConfigScreen(Screen parent) {
        var builder = YetAnotherConfigLib.createBuilder()
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
                                .option(Option.<Boolean>createBuilder()
                                        .name(txt("Server Toast"))
                                        .description(OptionDescription.of(txt("Creates a toast upon joining a server with BeansUtils installed server-side.")))
                                        .binding(ModClientConfig.DEFAULTS.getSendServerConnectToast(), () -> ModClientConfig.INSTANCE.getSendServerConnectToast(), v -> ModClientConfig.INSTANCE.setSendServerConnectToast(v))
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
                        .build());

        MutableServerConfig serverConfig = BeansUtilsClient.SERVER_CONFIG;

        boolean isWorld = MinecraftClient.getInstance().world != null && serverConfig != null;

        if (isWorld) {
            BeansUtils.LOGGER.info("Generating Server Config...");
            builder.category(
                    ConfigCategory.createBuilder()
                            .name(txt("Server"))

                            // ===== Mod Settings =====
                            .group(
                                    OptionGroup.createBuilder()
                                            .name(txt("Mod Settings"))
                                            .option(getBooleanOption(
                                                    "Require Mod",
                                                    "Whether clients must have the mod installed",
                                                    "mod_settings.require_mod",
                                                    serverConfig
                                            ).build())
                                            .option(getStringOption(
                                                    "Kick Message",
                                                    "Kick message for players without the mod",
                                                    "mod_settings.kick_message",
                                                    serverConfig
                                            ).build())
                                            .option(getIntegerOption(
                                                    "Handshake Timeout",
                                                    "How long to wait before disconnecting laggy handshakes.",
                                                    1, 30,
                                                    "mod_settings.handshake_timeout",
                                                    serverConfig
                                            ).build())
                                            .build()
                            )

                            // ===== Server Settings =====
                            .group(
                                    OptionGroup.createBuilder()
                                            .name(txt("Server Settings"))
                                            .option(getBooleanOption(
                                                    "PvP Enabled",
                                                    "Whether players can damage each other.",
                                                    "server.pvp_enabled",
                                                    serverConfig
                                            ).build())
                                            .build()
                            )

                            // ===== Resourcepack Settings =====
                            .group(
                                    OptionGroup.createBuilder()
                                            .name(txt("Resourcepack Settings"))
                                            .option(getBooleanOption(
                                                    "Use Custom Resourcepack",
                                                    "Whether to send a custom resourcepack to clients.",
                                                    "server.resourcepack_settings.use_custom_resourcepack",
                                                    serverConfig
                                            ).build())
                                            .option(getStringOption(
                                                    "Custom Resourcepack URL",
                                                    "Direct URL to your resourcepack file.",
                                                    "server.resourcepack_settings.custom_resourcepack_url",
                                                    serverConfig
                                            ).build())
                                            .option(getStringOption(
                                                    "Custom Resourcepack Hash",
                                                    "SHA-1 hash for verifying the resourcepack.",
                                                    "server.resourcepack_settings.custom_hash",
                                                    serverConfig
                                            ).build())
                                            .build()
                            )

                            // ===== Vanilla Feature Toggling =====
                            .group(
                                    OptionGroup.createBuilder()
                                            .name(txt("Vanilla Feature Toggling"))
                                            .option(getBooleanOption(
                                                    "Nether Portals Disabled",
                                                    "Prevents use of Nether portals.",
                                                    "vanilla_feature_toggling.nether_portals_disabled",
                                                    serverConfig
                                            ).build())
                                            .option(getBooleanOption(
                                                    "End Portals Disabled",
                                                    "Prevents access to the End.",
                                                    "vanilla_feature_toggling.end_portals_disabled",
                                                    serverConfig
                                            ).build())
                                            .option(getBooleanOption(
                                                    "Eyes of Ender Disabled",
                                                    "Stops Eyes of Ender from working.",
                                                    "vanilla_feature_toggling.eyes_of_ender_disabled",
                                                    serverConfig
                                            ).build())
                                            .build()
                            )

                            // ===== Pet Respawning =====
                            .group(
                                    OptionGroup.createBuilder()
                                            .name(txt("Pet Respawning"))
                                            .option(getBooleanOption(
                                                    "Pet Respawning Enabled",
                                                    "Whether pets automatically respawn.",
                                                    "pet_respawning.petRespawningEnabled",
                                                    serverConfig
                                            ).build())
                                            .option(getIntegerOption(
                                                    "Respawn Delay",
                                                    "Time before respawn (seconds).",
                                                    1, 600,
                                                    "pet_respawning.respawnDelay",
                                                    serverConfig
                                            ).build())
                                            .build()
                            )

                            // ===== Combat Tagging =====
                            .group(
                                    OptionGroup.createBuilder()
                                            .name(txt("Combat Tagging"))
                                            .option(getBooleanOption(
                                                    "Combat Tagging Enabled",
                                                    "Enables the combat tag system.",
                                                    "combat_tagging.combat_tagging_enabled",
                                                    serverConfig
                                            ).build())
                                            .option(getIntegerOption(
                                                    "Combat Duration",
                                                    "Duration players stay tagged (seconds).",
                                                    1, 300,
                                                    "combat_tagging.combat_duration",
                                                    serverConfig
                                            ).build())
                                            .option(getBooleanOption(
                                                    "Kill On Combat Logging",
                                                    "Players die if they leave during combat.",
                                                    "combat_tagging.kill_players_upon_combat_logging",
                                                    serverConfig
                                            ).build())
                                            .option(LabelOption.create(txt("View config file for CombatTriggeringEntities")))
                                            .build()
                            )

                            // ===== Combat Disabled Features =====
                            .group(
                                    OptionGroup.createBuilder()
                                            .name(txt("Combat Disabled Features"))
                                            .option(getBooleanOption(
                                                    "Disable Ender Pearls",
                                                    "Cannot use ender pearls while in combat.",
                                                    "combat_tagging.combat_disabled_features.disable_ender_pearls",
                                                    serverConfig
                                            ).build())
                                            .option(getBooleanOption(
                                                    "Disable Firework Rockets",
                                                    "Prevents elytra boosting during combat.",
                                                    "combat_tagging.combat_disabled_features.disable_firework_rockets",
                                                    serverConfig
                                            ).build())
                                            .option(getBooleanOption(
                                                    "Disable Tridents",
                                                    "Blocks trident throwing while tagged.",
                                                    "combat_tagging.combat_disabled_features.disable_tridents",
                                                    serverConfig
                                            ).build())
                                            .build()
                            )

                            // ===== TPA =====
                            .group(
                                    OptionGroup.createBuilder()
                                            .name(txt("TPA"))
                                            .option(getBooleanOption(
                                                    "TPA Enabled",
                                                    "Allows players to use TPA commands.",
                                                    "tpa.tpa_enabled",
                                                    serverConfig
                                            ).build())
                                            .option(getIntegerOption(
                                                    "TPA Timeout",
                                                    "Seconds before a TPA request expires.",
                                                    1, 300,
                                                    "tpa.tpa_timeout",
                                                    serverConfig
                                            ).build())
                                            .option(getIntegerOption(
                                                    "TPA Experience Requirement",
                                                    "Required XP levels to send a TPA.",
                                                    0, 100,
                                                    "tpa.tpa_exp_requirement",
                                                    serverConfig
                                            ).build())
                                            .build()
                            )

                            .build()
            );
        }

        return builder.save(() -> {
            BeansUtils.LOGGER.info("SAVING CONFIG... (Server: {})", serverConfig != null);
            ModClientConfig.INSTANCE.save();
            if (serverConfig != null) serverConfig.pushUpdate();
        }).build().generateScreen(parent);
    }

    private static <T> Option.Builder<T> getOption(Class<T> clazz, String name, String desc, String key, MutableServerConfig config, T fallback) {
        return Option.<T>createBuilder()
                .name(txt(name))
                .description(OptionDescription.of(txt(desc)))
                .binding(config.getByPath(key, fallback), () -> config.getByPath(key, fallback), v -> config.setByPath(key, v));
    }

    private static Option.Builder<Boolean> getBooleanOption(String name, String desc, String key, MutableServerConfig config) {
        return getOption(Boolean.class, name, desc, key, config, false)
                .controller(YACLConfigScreen::createEnableDisableCheckbox);

    }

    private static Option.Builder<String> getStringOption(String name, String desc, String key, MutableServerConfig config) {
        return getOption(String.class, name, desc, key, config, "")
                .controller(StringControllerBuilder::create);
    }

    private static Option.Builder<Integer> getIntegerOption(String name, String desc, Integer min, Integer max, String key, MutableServerConfig config) {
        return Option.<Integer>createBuilder()
                .name(txt(name))
                .description(OptionDescription.of(txt(desc)))
                .binding(config.getByPath(key, 3).intValue(), () -> config.getByPath(key, 3).intValue(), v -> config.setByPath(key, v.intValue()))
                .controller(option -> IntegerSliderControllerBuilder.create(option).range(min, max).step(1));
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
