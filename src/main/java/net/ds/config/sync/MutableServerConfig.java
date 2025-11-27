package net.ds.config.sync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.ds.BeansUtils;
import net.ds.network.ConfigSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MutableServerConfig {
    private static final String[] DOUBLE_TO_INT = {"pet_respawning.respawnDelay", "combat_tagging.combat_duration", "tpa.tpa_timeout", "mod_settings.handshake_timeout"};

    private final Map<String, Object> serverConfig = new HashMap<>();
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private boolean hasBeenUpdated = false;
    private boolean hasReceived = false;

    public MutableServerConfig() {}

    public boolean isPresent() {return hasReceived;}

    public void clearServerConfig() {
        hasBeenUpdated = false;
        hasReceived = false;
        serverConfig.clear();
    }

    public void onReceivedServerConfig(byte[] bytes) {
        hasReceived = true;
        String json = new String(bytes, StandardCharsets.UTF_8);
        Map<String, Object> map = GSON.fromJson(json,
                new TypeToken<Map<String, Object>>() {
                }.getType());

        //changing doubles to int
        map = (Map<String, Object>) SyncUtils.fixNumber(map);

        serverConfig.clear();
        serverConfig.putAll(map);
        BeansUtils.LOGGER.info("Synced Full Server Config");
    }

    public <T> T getByPath(String path, T fallback) {
        String[] parts = path.split("\\.");

        Object current = serverConfig;

        for (String part : parts) {
            if (!(current instanceof Map<?, ?> map)) {
                return fallback;
            }

            current = map.get(part);
            if (current == null) {
                return fallback;
            }
        }

        return (T) current;
    }

    public void setByPath(String path, Object value) {
        hasBeenUpdated = true;
        String[] parts = path.split("\\.");
        Map<String, Object> current = serverConfig;

        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);

            if (!(next instanceof Map<?, ?>)) {
                next = new java.util.HashMap<String, Object>();
                current.put(parts[i], next);
            }

            current = (Map<String, Object>) next;
        }

        current.put(parts[parts.length - 1], value);
    }

    public void set(String key, Object value) {
        BeansUtils.LOGGER.info("Setting {} to {}", key, value);
        hasBeenUpdated = true;
        serverConfig.put(key, value);
    }

    public void pushUpdate() {
        if (!hasBeenUpdated) {
            BeansUtils.LOGGER.info("No server config update; not pushing");
            return;
        }
        hasBeenUpdated = false;
        BeansUtils.LOGGER.info("Pushing server update.");

        String json = GSON.toJson(serverConfig);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        ClientPlayNetworking.send(new ConfigSyncPayload.ServerConfigC2SPayload(bytes));
    }

}
