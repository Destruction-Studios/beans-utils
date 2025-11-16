package net.ds.dedicated;

import net.ds.config.ModServerConfig;
import net.fabricmc.api.DedicatedServerModInitializer;

public class DedicatedModInit implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ModServerConfig.INSTANCE.reloadConfigFile();
    }
}
