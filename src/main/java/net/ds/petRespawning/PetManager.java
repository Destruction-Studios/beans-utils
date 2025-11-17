package net.ds.petRespawning;

import net.ds.interfaces.IPlayerDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PetManager {
    public static final String PET_NBT_KEY = "BeansUtils-petRespawning";

    public static void addRespawningPet(IPlayerDataSaver player) {
        NbtCompound data = player.beans_utils$persistentPetData();
//        List<> petsList = data.getListOrEmpty("pets");
//        petsList.add(List.of())
    }

    public static void respawnPet() {

    }

    public static void playSetRespawnEffect(Entity entity) {
        if (entity.getEntityWorld().isClient()) {
            return;
        }
        ServerWorld world = (ServerWorld) entity.getEntityWorld();
        BlockPos blockPos = entity.getBlockPos();

        world.playSound(null, blockPos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.PLAYERS, 1.0f, 1.0f);
        world.playSound(null, blockPos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.5f, 1.0f);

        world.spawnParticles(ParticleTypes.SOUL, entity.getX(), entity.getY(), entity.getZ(), 35, .05, 0.05, 0.05, .05);

    }
}
