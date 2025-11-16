package net.ds.util;

import com.mojang.brigadier.context.CommandContext;
import net.ds.BeansUtils;
import net.ds.config.ModServerConfig;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class Util {
    public static String tickToString(int tick) {
        return String.format("%.1f", (float) tick / 20);
    }

    public static boolean isResourcePackUrlOverrideSet () {
        return ModServerConfig.INSTANCE.getUseCustomResourcePack() && !Objects.equals(ModServerConfig.INSTANCE.getCustomResourcePackURL(), "");
    }

    public static String getSha1FromFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            String sha1 = DigestUtils.sha1Hex(fis);
            fis.close();
            return sha1;
        } catch (Exception e) {
            return "";
        }
    }

    public static void fetchHash(ServerCommandSource source) {
        try {
            if (Objects.equals(ModServerConfig.INSTANCE.getCustomResourcePackURL(), "")) {
                BeansUtils.LOGGER.warn("No custom url set");
                source.sendMessage(Text.literal("No custom url set").withColor(Colors.RED));
                return;
            }
            String url = ModServerConfig.INSTANCE.getCustomResourcePackURL();
            source.sendMessage(Text.literal("Downloading: " + url).withColor(Colors.BLUE));
            BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest);
            while (digestInputStream.read() != -1) {
            }
            digestInputStream.close();

            BeansUtils.LOGGER.info("Resource pack downloaded, calculating hash");
            byte[] hash = digest.digest();
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            String hashFinal = hexString.toString().toLowerCase(Locale.ROOT);

            ModServerConfig.INSTANCE.setCustomHash(hashFinal);
            ModServerConfig.INSTANCE.save();

            BeansUtils.LOGGER.info("Got hash: {}", hashFinal);
            source.sendMessage(Text.literal("Successfully fetched hash: " + hashFinal).withColor(Colors.GREEN));
        } catch (NoSuchElementException e) {
            BeansUtils.LOGGER.error("Could not get resource pack url: {}", String.valueOf(e));
        } catch (MalformedURLException e) {
            BeansUtils.LOGGER.error("Invalid resource pack url: {}", String.valueOf(e));
        } catch (IOException e) {
            BeansUtils.LOGGER.error("IOException: {}", String.valueOf(e));
        } catch (NoSuchAlgorithmException e) {
            BeansUtils.LOGGER.error("Invalid alg: {}", String.valueOf(e));
        }
    }

    public static void reloadAll(CommandContext<ServerCommandSource> context) {
        Optional<MinecraftServer.ServerResourcePackProperties> props = context.getSource().getServer().getResourcePackProperties();
        if (props.isPresent()) {
            String url = props.get().url();
            if (isResourcePackUrlOverrideSet()) {
                url = ModServerConfig.INSTANCE.getCustomResourcePackURL();
            }
            Text prompt = Text.of("Reloading resource packs..");
            for (var player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                player.networkHandler.sendPacket(new ResourcePackSendS2CPacket(props.get().id(), url, props.get().hash(), true, Optional.of(prompt)));
            }
        }
    }
}
