package net.ds.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.reflect.TypeToken;
import net.ds.BeansUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BeansUtilsExtension implements CarpetExtension {
    public static SettingsManager settingsManager;

    @Override
    public void onGameStarted() {
        ModContainer mod = FabricLoader.getInstance().getModContainer(BeansUtils.MOD_ID).orElseThrow(NullPointerException::new);

        settingsManager = new SettingsManager(mod.getMetadata().getVersion().getFriendlyString(), BeansUtils.MOD_ID, mod.getMetadata().getName());

        settingsManager.parseSettingsClass(BeansUtilsSettings.class);
        CarpetServer.settingsManager.parseSettingsClass(BeansUtilsSettings.class);
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        InputStream langFile = BeansUtils.class.getClassLoader().getResourceAsStream("assets/beans-utils/lang/%s.json".formatted(lang));
        if (langFile == null) {
            return Collections.emptyMap();
        }
        String jsonData;
        try {
            jsonData = IOUtils.toString(langFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).create();

        Map<String, String> map = gson.fromJson(jsonData, new TypeToken<Map<String, String>>() {}.getType());
        Map<String, String> map2 = new HashMap<>();

        // create translation keys for both carpet and beans-utils settingsManagers
        map.forEach((key, value) -> {
            map2.put(key, value);
            if(key.startsWith("beans-utils.rule.")) {
                map2.put(key.replace("beans-utils.rule.", "carpet.rule."), value);
            }
        });

        return map2;
    }
}
