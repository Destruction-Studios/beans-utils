package net.ds.config;

import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.NonSync;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import net.ds.BeansUtils;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Version(version = 2)
public class BeansUtilsClientConfig extends Config {
    public BeansUtilsClientConfig() {
        super(Identifier.of(BeansUtils.MOD_ID, "client_config"));
    }

    @Comment("Disables the leave button when in commbat.")
    public boolean preventLeavingWhenInCombat = true;
    @Comment("Does not respond to server handshake. Use for Debugging")
    public boolean rejectHandshake = false;

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
