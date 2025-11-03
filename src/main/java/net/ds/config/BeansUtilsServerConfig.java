package net.ds.config;

import carpet.script.api.Entities;
import me.fzzyhmstrs.fzzy_config.annotations.NonSync;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedIdentifierMap;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.ds.BeansUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

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

        public boolean combatLoggingEnabled = false;
        public ValidatedInt combatDuration = new ValidatedInt(15, 120, 3);

        public ValidatedIdentifierMap<Float> combatTriggeringEntities = new ValidatedIdentifierMap(
               new LinkedHashMap<>(),
                ValidatedIdentifier.ofRegistry(Identifier.of("warden"), Registries.ENTITY_TYPE),
                new ValidatedInt(1, 5, 1)
        );
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
