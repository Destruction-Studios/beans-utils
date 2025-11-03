package net.ds.config;

import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedIdentifierMap;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.ds.BeansUtils;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@Version(version = 1)
public class BeansUtilsServerConfig extends Config {
    public BeansUtilsServerConfig() {
        super(Identifier.of(BeansUtils.MOD_ID, "server_config"));
    }

    public CombatLogging combatLogging = new CombatLogging();

    public static class CombatLogging extends ConfigSection{
        public CombatLogging() {
            super();
        }
        @Comment("Whether or not combat logging features are enabled")
        public boolean combatTaggingEnabled = false;
        @Comment("How long a player is in combat. (Seconds)")
        public ValidatedInt combatDuration = new ValidatedInt(15, 120, 3);
        @Comment("Should a player be killed upon combat logging")
        public boolean killPlayerUponCombatLogging = true;
        @Comment("What entities cause combat")
        public ValidatedIdentifierMap<Float> combatTriggeringEntities = new ValidatedIdentifierMap(
                Map.of(Identifier.of("player"), 1),
                ValidatedIdentifier.ofRegistry(Identifier.of("player"), Registries.ENTITY_TYPE),
                new ValidatedInt(1, 5, 1)
        );

        public CombatDisabledFeatures featuresDisabledInCombat = new CombatDisabledFeatures();
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
    public void update(int deserializedVersion) {

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
