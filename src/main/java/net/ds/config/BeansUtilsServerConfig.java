package net.ds.config;

import me.fzzyhmstrs.fzzy_config.annotations.*;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigAction;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedIdentifierMap;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.ds.BeansUtils;
import net.ds.network.SetHashPayload;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

@Version(version = 10)
public class BeansUtilsServerConfig extends Config {
    public BeansUtilsServerConfig() {
        super(Identifier.of(BeansUtils.MOD_ID, "server_config"));
    }

    public ConfigGroup clientModVerification = new ConfigGroup("clientModVerification");

    @Comment("If players without the mod installed will be kicked.")
    public boolean requireMod = false;
    @Comment("The kick message a player gets without the mod.")
    @MultilineString
    public String noModDisconnectMessage = "This server requires BeansUtils";
    @Comment("The max amount of time a player can not respond before being kicked.")
    public int handshakeTimeout = 200;
    @Comment("Send a message to a player without the mod recommending it.")
    public boolean notifyPlayersWithNoMod = false;
    @Comment("Message a player without the mod is notified with")
    @MultilineString
    @ConfigGroup.Pop
    public String notifyPlayerMessage = "This server recommends you install BeansUtils for more functionality.";

    public ConfigGroup serverSettings = new ConfigGroup("serverSettings");

    @Comment("Settings relating to custom resource packs.")
    public ResourcePackSettings resourcePackSettings = new ResourcePackSettings();

    @Comment("Whether or not player vs player combat is enabled.")
    @ConfigGroup.Pop
    public boolean pvpEnabled = true;

    public ConfigGroup vanillaFeatureToggling = new ConfigGroup("vanillaFeatureToggling");

    @Comment("Whether or not Nether Portals are disabled.")
    public boolean netherPortalsDisabled = false;
    @Comment("Whether or not End Portals are disabled.")
    public boolean endPortalsDisabled = false;
    @Comment("Whether or not Eyes of Ender are disabled.")
    @ConfigGroup.Pop
    public boolean eyesOfEnderDisabled = false;

    public ConfigGroup combatTagging = new ConfigGroup("combatTagging");

    @Comment("Whether or not combat logging features are enabled")
    public boolean combatTaggingEnabled = false;
    @Comment("How long a player is in combat. (Seconds)")
    public ValidatedInt combatDuration = new ValidatedInt(15, 120, 3);
    @Comment("Should a player be killed upon combat logging")
    public boolean killPlayerUponCombatLogging = true;
    @Comment("What entities cause combat")
    public ValidatedIdentifierMap<Boolean> combatTriggeringEntities = new ValidatedIdentifierMap<>(
            Map.of(Identifier.of("player"), true),
            ValidatedIdentifier.ofRegistry(Identifier.of("player"), Registries.ENTITY_TYPE),
            new ValidatedBoolean(true)
    );

    @ConfigGroup.Pop
    @Comment("Features disabled while a player is in combat.")
    public CombatDisabledFeatures combatDisabledFeatures = new CombatDisabledFeatures();

    public static class ResourcePackSettings extends ConfigSection{
        public ResourcePackSettings() {super();}

        private final Supplier<Text> setHashLabel = () -> Text.of("Set Hash");
        private final Supplier<Boolean> setHashEnabled = () -> true;
        private final Runnable setHashRunnable = SetHashPayload::requestHashReset;

        @Comment("Sets the servers hash to the resource pack set below")
        @Prefix("!! DO NOT SPAM !!\nCan be used for custom resource packs or resource packs set in 'server.properties'")
        public ConfigAction setHash = new ConfigAction(setHashLabel, setHashEnabled, setHashRunnable, null, null);
        @Comment("Whether or not players are send the custom resource pack.")
        public boolean useCustomResourcePack = false;
        @Comment("The resource pack of the server.")
        @LiteralString
        public String serverResourcePackURL = "";
    }

    public static class CombatDisabledFeatures extends ConfigSection{
        public CombatDisabledFeatures() {super();}

        @Comment("Are ender pearls disabled in combat")
        public boolean disableEnderPearls = false;
        @Comment("Are riptide tridents disabled in combat")
        public boolean disableTridents = false;
        @Comment("Are firework rockets disabled in combat")
        public boolean disabledFireworkRockets = false;
    }

    @Override
    public int defaultPermLevel() {
        return 4;
    }

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON5;
    }

    @Override
    public @NotNull SaveType saveType() {
        return SaveType.SEPARATE;
    }
}
