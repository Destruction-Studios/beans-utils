package net.ds.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.ds.BeansUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ModServerConfig {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    public static final Path CONFIG_FILE = Path.of("config").resolve(BeansUtils.MOD_ID).resolve("server_config.json");
    public static final ModServerConfig DEFAULTS = new ModServerConfig();
    public static ModServerConfig INSTANCE = load(CONFIG_FILE.toFile());

    @SerializedName("mod_settings") ModSettings modSettings = new ModSettings();

    static class ModSettings {
        @SerializedName("require_mod") boolean requireMod = false;
        @SerializedName("kick_message") String kickMessage = "This server requires BeansUtils.";
        @SerializedName("handshake_timeout") int handshakeTimeout = 3;
    }

    @SerializedName("server") Server server = new Server();
    static class Server {
        @SerializedName("pvp_enabled") boolean pvpEnabled = true;
        @SerializedName("resourcepack_settings") ResourcePackSettings resourcePackSettings = new ResourcePackSettings();

        static class ResourcePackSettings {
            @SerializedName("use_custom_resourcepack") boolean useCustomResourcePack = false;
            @SerializedName("custom_resourcepack_url") String customResourcePackURL = "";
            @SerializedName("custom_hash") String customHash = "";
        }
    }

    @SerializedName("vanilla_feature_toggling") VanillaFeatures vanillaFeaturesToggling = new VanillaFeatures();
    static class VanillaFeatures {
        @SerializedName("nether_portals_disabled") boolean netherPortalsDisabled = false;
        @SerializedName("end_portals_disabled") boolean endPortalsDisabled = false;
        @SerializedName("eyes_of_ender_disabled") boolean eyesOfEnderDisabled = false;
    }

    @SerializedName("pet_respawning") PetRespawning petRespawning = new PetRespawning();
    static class PetRespawning {
        boolean petRespawningEnabled = false;
        int respawnDelay = 120;
    }

    @SerializedName("combat_tagging") CombatTagging combatTagging = new CombatTagging();
    static class CombatTagging {
        @SerializedName("combat_tagging_enabled") boolean combatTaggingEnabled = false;
        @SerializedName("combat_duration") int combatDuration = 15;
        @SerializedName("kill_players_upon_combat_logging") boolean killPlayerUponCombatLogging = true;
        @SerializedName("combat_triggering_entities") List<String> combatTriggeringEntities = List.of("minecraft:player");

        @SerializedName("combat_disabled_features") CombatDisabledFeatures combatDisabledFeatures = new CombatDisabledFeatures();
        static class CombatDisabledFeatures {
            @SerializedName("disable_ender_pearls") boolean disabledEnderPearls = false;
            @SerializedName("disable_firework_rockets") boolean disableFireworkRockets = false;
            @SerializedName("disable_tridents") boolean disableTridents = false;
        }
    }

    @SerializedName("tpa") TPA tpa = new TPA();
    static class TPA {
        @SerializedName("tpa_enabled") boolean tpaEnabled = false;
        @SerializedName("tpa_timeout") int tpaTimeout = 10;
        @SerializedName("tpa_exp_requirement") int tpaExpRequirement = 2;
    }

    //thx chatgpt for making these getters and setters sooo helpful!

    public boolean getRequireMod() { return modSettings.requireMod; }
    public void setRequireMod(boolean value) { modSettings.requireMod = value; }

    public String getKickMessage() { return modSettings.kickMessage; }
    public void setKickMessage(String value) { modSettings.kickMessage = value; }

    public int getHandshakeTimeout() { return modSettings.handshakeTimeout * 20; }
    public void setHandshakeTimeout(int value) { modSettings.handshakeTimeout = value; }

    // ===== Server =====
    public boolean getPvpEnabled() { return server.pvpEnabled; }
    public void setPvpEnabled(boolean value) { server.pvpEnabled = value; }

    public boolean getUseCustomResourcePack() { return server.resourcePackSettings.useCustomResourcePack; }
    public void setUseCustomResourcePack(boolean value) { server.resourcePackSettings.useCustomResourcePack = value; }

    public String getCustomResourcePackURL() { return server.resourcePackSettings.customResourcePackURL; }
    public void setCustomResourcePackURL(String value) { server.resourcePackSettings.customResourcePackURL = value; }

    public String getCustomHash() { return server.resourcePackSettings.customHash; }
    public void setCustomHash(String value) { server.resourcePackSettings.customHash = value; }

    // ===== VanillaFeatures =====
    public boolean getNetherPortalsDisabled() { return vanillaFeaturesToggling.netherPortalsDisabled; }
    public void setNetherPortalsDisabled(boolean value) { vanillaFeaturesToggling.netherPortalsDisabled = value; }

    public boolean getEndPortalsDisabled() { return vanillaFeaturesToggling.endPortalsDisabled; }
    public void setEndPortalsDisabled(boolean value) { vanillaFeaturesToggling.endPortalsDisabled = value; }

    public boolean getEyesOfEnderDisabled() { return vanillaFeaturesToggling.eyesOfEnderDisabled; }
    public void setEyesOfEnderDisabled(boolean value) { vanillaFeaturesToggling.eyesOfEnderDisabled = value; }

    // ===== Pets =====
    public boolean getPetRespawningEnabled() { return petRespawning.petRespawningEnabled; }
    public void setPetRespawningEnabled(boolean value) { petRespawning.petRespawningEnabled = value; }

    public int getRespawnDelay() { return petRespawning.respawnDelay * 20; }
    public void setRespawnDelay(int value) { petRespawning.respawnDelay = value; }

    // ===== CombatTagging =====
    public boolean getCombatTaggingEnabled() { return combatTagging.combatTaggingEnabled; }
    public void setCombatTaggingEnabled(boolean value) { combatTagging.combatTaggingEnabled = value; }

    public int getCombatDuration() { return combatTagging.combatDuration; }
    public void setCombatDuration(int value) { combatTagging.combatDuration = value; }

    public boolean getKillPlayerUponCombatLogging() { return combatTagging.killPlayerUponCombatLogging; }
    public void setKillPlayerUponCombatLogging(boolean value) { combatTagging.killPlayerUponCombatLogging = value; }

    public List<String> getCombatTriggeringEntities() { return combatTagging.combatTriggeringEntities; }
    public void setCombatTriggeringEntities(List<String> value) { combatTagging.combatTriggeringEntities = value; }

    public boolean getDisabledEnderPearls() { return combatTagging.combatDisabledFeatures.disabledEnderPearls; }
    public void setDisabledEnderPearls(boolean value) { combatTagging.combatDisabledFeatures.disabledEnderPearls = value; }

    public boolean getDisableFireworkRockets() { return combatTagging.combatDisabledFeatures.disableFireworkRockets; }
    public void setDisableFireworkRockets(boolean value) { combatTagging.combatDisabledFeatures.disableFireworkRockets = value; }

    public boolean getDisableTridents() { return combatTagging.combatDisabledFeatures.disableTridents; }
    public void setDisableTridents(boolean value) { combatTagging.combatDisabledFeatures.disableTridents = value; }

    // ===== TPA =====
    public boolean getTpaEnabled() { return tpa.tpaEnabled; }
    public void setTpaEnabled(boolean v) { tpa.tpaEnabled = v; }

    public int getTpaTimeout() { return tpa.tpaTimeout * 20; }
    public void setTpaTimeout(int v) { tpa.tpaTimeout = v; }

    public int getTpaExpRequirement() { return tpa.tpaExpRequirement; }
    public void setTpaExpRequirement(int v) { tpa.tpaExpRequirement = v; }

    public void reloadConfigFile() {
        INSTANCE = load(CONFIG_FILE.toFile());
    }

    public void save() {
        saveConfigFile(CONFIG_FILE.toFile());
    }

    private void saveConfigFile(File file) {
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error saving config: " + e);
        }
    }

    private static ModServerConfig load(File file) {
        ModServerConfig config = null;

        try {
            Files.createDirectories(file.getParentFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException("Error creating directories: " + e);
        }

        if (file.exists()) {
            try(BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                config = GSON.fromJson(reader, ModServerConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Error loading config: " + e);
            }
        }

        if (config == null) {
            config = new ModServerConfig();
        }

        config.saveConfigFile(file);
        return config;
    }

    public static void applyUpdates(Map<String, Object> des) {
        mergeInto(INSTANCE, des);
        INSTANCE.save();
        BeansUtils.LOGGER.info("updated... refreshing");
        BeansUtils.refreshConfigToAll(serialize());
    }

    @SuppressWarnings("unchecked")
    private static void mergeInto(Object targetObject, Map<String, Object> updates) {
        Class<?> clazz = targetObject.getClass();

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String key = entry.getKey();
            Object newValue = entry.getValue();

            try {
                var field = findFieldBySerializedName(clazz, key);
                if (field == null) {
                    BeansUtils.LOGGER.error("Field not found for key {}", key);
                    continue;
                }
                field.setAccessible(true);

                Object currentValue = field.get(targetObject);

                // If both sides are maps, recurse into nested object
                if (newValue instanceof Map && currentValue != null) {
                    mergeInto(
                            currentValue,
                            (Map<String, Object>) newValue
                    );
                    continue;
                }

                // Fix gson double -> int garbage
                Class<?> type = field.getType();
                if (type == int.class || type == Integer.class) {
                    if (newValue instanceof Number n) {
                        newValue = n.intValue();
                    }
                }

                if (type == boolean.class || type == Boolean.class) {
                    if (newValue instanceof Boolean b) {
                        // ok
                    } else if (newValue instanceof Number n) {
                        newValue = n.intValue() != 0;
                    }
                }

                field.set(targetObject, newValue);

            } catch (Exception ex) {
                BeansUtils.LOGGER.error("Failed to apply key {}: {}", key, ex.getMessage());
            }
        }
    }

    private static Field findFieldBySerializedName(Class<?> clazz, String jsonName) {
        for (Field f : clazz.getDeclaredFields()) {
            SerializedName sn = f.getAnnotation(SerializedName.class);
            if (sn != null && sn.value().equals(jsonName)) {
                return f;
            }
            // fallback: actual field name matches
            if (f.getName().equals(jsonName)) {
                return f;
            }
        }
        return null;
    }


    public static byte[] serialize() {
        JsonObject jsonObject = GSON.toJsonTree(INSTANCE).getAsJsonObject();

        Map<String, Object> map = GSON.fromJson(jsonObject, new TypeToken<Map<String, Object>>() {}.getType());

        String jsonString = GSON.toJson(map);

        return jsonString.getBytes(StandardCharsets.UTF_8);
    }

    public static Map<String, Object> deserialize(byte[] bytes) {
        String json = new String(bytes, StandardCharsets.UTF_8);

        return GSON.fromJson(json, new com.google.gson.reflect.TypeToken<Map<String, Object>>() {}.getType());
    }
}
