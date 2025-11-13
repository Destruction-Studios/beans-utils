package net.ds.combatLog.func;

import net.ds.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SendCombatMessage {
    public static void SendInCombatMessage(PlayerEntity player, int combatTime) {
        player.sendMessage(Text.literal("You are in combat, do not leave! (" + Utils.tickToString(combatTime) + ")").fillStyle(Style.EMPTY.withBold(true).withColor(Formatting.RED)), true);
    }
    public static void SendLeaveCombatMessage(PlayerEntity player) {
        player.sendMessage(Text.literal("You are no longer in combat.").fillStyle(Style.EMPTY.withColor(Formatting.GREEN)), true);
    }
}
