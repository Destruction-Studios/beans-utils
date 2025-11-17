package net.ds.tpa;

import net.ds.config.ModServerConfig;
import net.ds.util.DelayedRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TPAManager {
    public static final ConcurrentHashMap<UUID, List<UUID>> playerTPAMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<UUID, List<UUID>> playerTPAHereMap = new ConcurrentHashMap<>();
    private static final ArrayList<DelayedRunnable> tpaRunnables = new ArrayList<>();

    public static List<UUID> listPlayerTPA (UUID player_uuid){
        return playerTPAMap.get(player_uuid);
    }
    public static List<UUID> listPlayerTPAHere (UUID player_uuid){
        return playerTPAHereMap.get(player_uuid);
    }

    public static void addRunnable(Runnable runnable) {
        tpaRunnables.add(new DelayedRunnable(ModServerConfig.INSTANCE.getTpaTimeout(), runnable));
    }

    public static void tick() {
        tpaRunnables.removeIf(event -> {
            if (event.isDone()) {
                return true;
            }
            event.tick();
            return false;
        });
    }
}
