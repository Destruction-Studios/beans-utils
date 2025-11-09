package net.ds.petRespawning;

import net.ds.interfaces.IEntityDataSaver;
import net.minecraft.nbt.NbtCompound;

public class PetData {
    public static final String PET_NBT_KEY = "BeansUtils-petRespawning";

    public static void addRespawningPet(IEntityDataSaver player) {
        NbtCompound data = player.beans_utils$persistentPetData();
    }

    public static void respawnPet() {

    }
}
