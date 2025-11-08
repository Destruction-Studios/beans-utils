package net.ds;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class Utils {
    public static String tickToString(int tick) {
        return String.format("%.1f", (float) tick / 20);
    }

    public static boolean isResourcePackUrlOverrideSet () {
        return BeansUtils.SERVER_CONFIG.resourcePackSettings.useCustomResourcePack && !Objects.equals(BeansUtils.SERVER_CONFIG.resourcePackSettings.serverResourcePackURL, "");
    }

    public static File getOrCreateHashFile() {
        File hashFile = new File("config/" + BeansUtils.MOD_ID + "/server_hash.txt");
        if(!hashFile.exists()) {
            BeansUtils.LOGGER.warn("Generating hash file...");
            try {
                boolean created = hashFile.createNewFile();
                if(created) {
                    FileWriter writer = new FileWriter(hashFile);
                    writer.write("");
                    writer.close();
                    BeansUtils.LOGGER.info("Generated hash file!");
                } else {
                    BeansUtils.LOGGER.error("Could not create config file!");
                }

            } catch(IOException ioException)
            {
                BeansUtils.LOGGER.error(ioException.toString());
            }
        }
        return hashFile;
    }
}
