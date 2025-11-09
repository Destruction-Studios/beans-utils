package net.ds.interfaces;

import net.minecraft.nbt.NbtCompound;

public interface IEntityDataSaver {
    NbtCompound beans_utils$persistentCombatData();
    NbtCompound beans_utils$persistentPetData();
}
