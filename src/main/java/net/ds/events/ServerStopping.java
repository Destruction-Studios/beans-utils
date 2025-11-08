package net.ds.events;

import net.ds.BeansUtils;
import net.ds.combatLog.CombatData;
import net.ds.combatLog.IEntityDataSaver;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerStopping implements ServerLifecycleEvents.ServerStopping {
    public static final ServerStopping INSTANCE = new ServerStopping();

    @Override
    public void onServerStopping(MinecraftServer minecraftServer) {
        BeansUtils.LOGGER.info("Server stopping, removing combat.");
        for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
            CombatData.endCombat((IEntityDataSaver) player);
        }
    }
}