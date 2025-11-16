package net.ds.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class ToastUtil {
    public static void toasty(Text text) {
        MinecraftClient.getInstance().getToastManager().add(SystemToast.create(
                MinecraftClient.getInstance(),
                SystemToast.Type.PERIODIC_NOTIFICATION,
                Text.of("Beans Utils"),
                text
        ));
    }
}
