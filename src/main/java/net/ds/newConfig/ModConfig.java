package net.ds.newConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ds.BeansUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ModConfig {
    public static final Gson GSON = new GsonBuilder()
            .addSerializationExclusionStrategy(new IgnoreValue.SerializationStrategy())
            .setPrettyPrinting()
            .create();
    public static final ModConfig DEFAULT = new ModConfig();
    public static final Path CONFIG_FILE = Path.of("config").resolve(BeansUtils.MOD_ID).resolve("client_config.json");
    public static ModConfig INSTANCE = loadConfigFile(CONFIG_FILE.toFile());


    int VERSION = 1;

    General general = new General();
    static class General {
        boolean preventLeavingInCombat = false;
    }

    Debug debug = new Debug();
    static class Debug{
        boolean ignoreServerHandshake = false;
    }

    public boolean getPreventLeavingInCombat() {
        return general.preventLeavingInCombat;
    }

    public boolean getIgnoreServerHandshake() {
        return debug.ignoreServerHandshake;
    }

    private static ModConfig loadConfigFile(File file) {
        ModConfig config = null;

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                config = GSON.fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Error occurred while loading BeansUtils config: ", e);
            }
        }

        if (config == null) {
            config = new ModConfig();
        }

        config.saveConfigFile(file);
        return config;
    }

    private void saveConfigFile(File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred saving BeansUtils config: ", e);
        }
    }
}
