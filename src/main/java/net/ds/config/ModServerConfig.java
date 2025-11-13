package net.ds.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ds.BeansUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ModServerConfig {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    public static final Path CONFIG_FILE = Path.of("config").resolve(BeansUtils.MOD_ID).resolve("server_config_v2.json");
    public static final ModServerConfig DEFAULTS = new ModServerConfig();
    public static ModServerConfig INSTANCE = load(CONFIG_FILE.toFile());

    ModSettings modSettings = new ModSettings();
    static class ModSettings {
        boolean requireMod = false;
        String kickMessage = "This server requires BeansUtils.";
        int handshakeTimeout = 200;
    }

    Server server = new Server();
    static class Server {
        boolean pvpEnabled = true;
        ResourcePackSettings resourcePackSettings = new ResourcePackSettings();

        static class ResourcePackSettings {
            boolean useCustomResourcePack = false;
            String customResourcePackURL = "";
        }
    }

    VanillaFeatures vanillaFeaturesToggling = new VanillaFeatures();
    static class VanillaFeatures {
        boolean netherPortalsDisabled = false;
        boolean endPortalsDisabled = false;
        boolean eyesOfEnderDisabled = false;
    }

    CombatTagging combatTagging = new CombatTagging();
    static class CombatTagging {
        boolean combatTaggingEnabled = false;
        int combatDuration = 15;
        boolean killPlayerUponCombatLogging = true;
        List<String> combatTriggeringEntities = List.of("minecraft:player");

        CombatDisabledFeatures combatDisabledFeatures = new CombatDisabledFeatures();
        static class CombatDisabledFeatures {
            boolean disabledEnderPearls = false;
            boolean disableFireworkRockets = false;
            boolean disableTridents = false;
        }
    }

    //thx chatgpt for making these getters and setters sooo helpful!

    public boolean getRequireMod() { return modSettings.requireMod; }
    public void setRequireMod(boolean value) { modSettings.requireMod = value; }

    public String getKickMessage() { return modSettings.kickMessage; }
    public void setKickMessage(String value) { modSettings.kickMessage = value; }

    public int getHandshakeTimeout() { return modSettings.handshakeTimeout; }
    public void setHandshakeTimeout(int value) { modSettings.handshakeTimeout = value; }

    // ===== Server =====
    public boolean getPvpEnabled() { return server.pvpEnabled; }
    public void setPvpEnabled(boolean value) { server.pvpEnabled = value; }

    public boolean getUseCustomResourcePack() { return server.resourcePackSettings.useCustomResourcePack; }
    public void setUseCustomResourcePack(boolean value) { server.resourcePackSettings.useCustomResourcePack = value; }

    public String getCustomResourcePackURL() { return server.resourcePackSettings.customResourcePackURL; }
    public void setCustomResourcePackURL(String value) { server.resourcePackSettings.customResourcePackURL = value; }

    // ===== VanillaFeatures =====
    public boolean getNetherPortalsDisabled() { return vanillaFeaturesToggling.netherPortalsDisabled; }
    public void setNetherPortalsDisabled(boolean value) { vanillaFeaturesToggling.netherPortalsDisabled = value; }

    public boolean getEndPortalsDisabled() { return vanillaFeaturesToggling.endPortalsDisabled; }
    public void setEndPortalsDisabled(boolean value) { vanillaFeaturesToggling.endPortalsDisabled = value; }

    public boolean getEyesOfEnderDisabled() { return vanillaFeaturesToggling.eyesOfEnderDisabled; }
    public void setEyesOfEnderDisabled(boolean value) { vanillaFeaturesToggling.eyesOfEnderDisabled = value; }

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
}
