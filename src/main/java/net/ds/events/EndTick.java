package net.ds.events;

import net.ds.BeansUtils;
import net.ds.combatLog.func.CombatTick;
import net.ds.tpa.TPAManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class EndTick implements ServerTickEvents.EndTick{
    public static final EndTick INSTANCE = new EndTick();

    @Override
    public void onEndTick(MinecraftServer server) {
        CombatTick.tick(server);
        TPAManager.tick();
        BeansUtils.handshakeServerTick(server);
    }
}
