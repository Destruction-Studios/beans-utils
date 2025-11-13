package net.ds.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ds.BeansUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModClientConfig {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    public static final Path CONFIG_FILE = Path.of("config").resolve(BeansUtils.MOD_ID).resolve("client_config_v2.json");
    public static final ModClientConfig DEFAULTS = new ModClientConfig();
    public static ModClientConfig INSTANCE = load(CONFIG_FILE.toFile());

    Server server = new Server();
    static class Server {
        boolean dontReloadResources = false;
    }

    CombatTagging combatTagging = new CombatTagging();
    static class CombatTagging {
        boolean preventLeavingInCombat = false;
    }

    Debug debug = new Debug();
    static class Debug{
        boolean ignoreHandshake = false;
    }

    public boolean getDontReloadResources() {
        return this.server.dontReloadResources;
    }
    public void setDontReloadResources(Boolean value) {
        this.server.dontReloadResources = value;
    }

    public boolean getPreventLeaving() {
        return this.combatTagging.preventLeavingInCombat;
    }
    public void setPreventLeaving(Boolean value) {
        this.combatTagging.preventLeavingInCombat = value;
    }

    public boolean getIgnoreHandshake() {
        return this.debug.ignoreHandshake;
    }
    public void setIgnoreHandshake(Boolean value) {
        this.debug.ignoreHandshake = value;
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

    private static ModClientConfig load(File file) {
        ModClientConfig config = null;

        try {
            Files.createDirectories(file.getParentFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException("Error creating directories: " + e);
        }

        if (file.exists()) {
            try(BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                config = GSON.fromJson(reader, ModClientConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Error loading config: " + e);
            }
        }

        if (config == null) {
            config = new ModClientConfig();
        }

        config.saveConfigFile(file);
        return config;
    }
}
