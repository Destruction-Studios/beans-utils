package net.ds.combatLog;

import net.ds.BeansUtils;
import net.ds.interfaces.IEntityDataSaver;
import net.minecraft.nbt.NbtCompound;

public class CombatData {
    public static final String COMBAT_LOG_NBT_KEY = "BeansUtils-combatLog";
    public static final String COMBAT_TIME = "combatTime";
    public static final String IN_COMBAT = "inCombat";

    public static void decreaseCombatTime(IEntityDataSaver player) {
        NbtCompound nbt = player.beans_utils$persistentCombatData();

        int combatTime = nbt.getInt(COMBAT_TIME, 0);
        if (combatTime > 0) {
            combatTime--;
            nbt.putInt(COMBAT_TIME, combatTime);
        }
    }

    public static void startCombat(IEntityDataSaver player) {
        NbtCompound nbt = player.beans_utils$persistentCombatData();
        nbt.putInt(COMBAT_TIME, BeansUtils.SERVER_CONFIG.combatDuration.get() * 20);
        nbt.putBoolean(IN_COMBAT, true);
    }

    public static void endCombat(IEntityDataSaver player) {
        NbtCompound nbt = player.beans_utils$persistentCombatData();
        nbt.putInt(COMBAT_TIME, 0);
        nbt.putBoolean(IN_COMBAT, false);
    }

    public static int getCombatTime(IEntityDataSaver player) {
        NbtCompound nbt = player.beans_utils$persistentCombatData();
        return nbt.getInt(COMBAT_TIME, 0);
    }

    public static boolean isInCombat(IEntityDataSaver player) {
        NbtCompound nbt = player.beans_utils$persistentCombatData();
        return nbt.getBoolean(IN_COMBAT,false);
    }
}
