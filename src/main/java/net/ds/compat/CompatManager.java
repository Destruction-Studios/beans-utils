package net.ds.compat;

import net.fabricmc.loader.api.FabricLoader;

public class CompatManager {
    public static boolean isYACLInstalled() {
        return checkModLoaded("yet_another_config_lib_v3");
    }

    public static boolean checkModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
