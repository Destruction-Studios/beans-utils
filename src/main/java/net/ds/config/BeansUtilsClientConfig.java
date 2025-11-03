package net.ds.config;

import me.fzzyhmstrs.fzzy_config.annotations.NonSync;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import net.ds.BeansUtils;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Version(version = 1)
public class BeansUtilsClientConfig extends Config {
    public BeansUtilsClientConfig() {
        super(Identifier.of(BeansUtils.MOD_ID, "client_config"));
    }

    public boolean preventLeavingWhenInCombat = true;

    @Override
    public void update(int deserializedVersion) {

    }

    @Override
    public int defaultPermLevel() {
        return 0;
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
