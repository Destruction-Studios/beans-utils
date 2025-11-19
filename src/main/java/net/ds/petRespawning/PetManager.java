package net.ds.petRespawning;

import net.ds.BeansUtils;
import net.ds.config.ModServerConfig;
import net.ds.interfaces.IPetDataSaver;
import net.ds.interfaces.IPlayerDataSaver;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class PetManager {
    public static final String PET_NBT_KEY = "BeansUtils-petRespawning";

    public static void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            IPlayerDataSaver dataSaver = (IPlayerDataSaver) player;
            NbtCompound data = dataSaver.beans_utils$persistentPetData();

            NbtList list = data.getListOrEmpty("pets");
            if (list.isEmpty()) continue;

            Iterator<NbtElement> iterator = list.iterator();
            while (iterator.hasNext()) {
                NbtElement e = iterator.next();
                if (e instanceof NbtCompound compound) {
                    Optional<Integer> optionalI = compound.getInt("ticks");
                     if (optionalI.isEmpty()) continue;

                     int ticks = optionalI.get();

                     if (ticks <= 0) {
                         BeansUtils.LOGGER.info("RESPAWNING PETTTT!!!");
                         respawnPet(compound, player);
                         iterator.remove();
                     } else {
                         compound.putInt("ticks", ticks - 1);
                     }
                }
            }
        }
    }

    public static void addRespawningPet(LivingEntity player, LivingEntity entity, Consumer<NbtCompound> consumer) {
        if (player.getEntityWorld().isClient()) {
            return;
        }
        IPlayerDataSaver dataSaver = (IPlayerDataSaver) (PlayerEntity) player;

        BeansUtils.LOGGER.info("ADDING NBT TO PLAYER FOR REPSAWN");

        NbtCompound nbtCompound = dataSaver.beans_utils$persistentPetData();
        NbtList list = nbtCompound.getListOrEmpty("pets");

        NbtCompound data = new NbtCompound();

        data.put("id", Identifier.CODEC, Registries.ENTITY_TYPE.getId(entity.getType()));
        data.putInt("ticks", ModServerConfig.INSTANCE.getRespawnDelay());
        data.putDouble("health", entity.getAttributeValue(EntityAttributes.MAX_HEALTH));
        if (Objects.nonNull(entity.getCustomName())) {
            data.putString("name", entity.getCustomName().getString());
        }
        consumer.accept(data);

        list.add(data);
        nbtCompound.put("pets", list);

        BeansUtils.LOGGER.info(list.toString());
        BeansUtils.LOGGER.info(nbtCompound.toString());
    }

    public static void respawnPet(NbtCompound data, PlayerEntity player) {
        if (player.getEntityWorld().isClient()) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) player.getEntityWorld();

        Identifier id = data.get("id", Identifier.CODEC).orElseThrow();
        Double health = data.getDouble("health").orElseThrow();
        Optional<String> customName = data.getString("name");

        EntityType<?> entityType = Registries.ENTITY_TYPE.get(id);
        LivingEntity entity = (LivingEntity) entityType.create(serverWorld, SpawnReason.NATURAL);
        assert entity != null;

        Objects.requireNonNull(entity.getAttributeInstance(EntityAttributes.MAX_HEALTH)).setBaseValue(health);
        customName.ifPresent((value) -> entity.setCustomName(Text.of(value)));

        if (entityType == EntityType.WOLF) {
            WolfEntity wolf = (WolfEntity) entity;
            wolf.setOwner(player);
//            data.get("collar_color", DyeColor.CODEC).ifPresent((value) -> wolf.getDataTracker().set(WolfEntity., value.getIndex()));
        }

        entity.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0, 0);
        serverWorld.spawnEntity(entity);
        playSetRespawnEffect(entity);
    }

    public static void playSetRespawnEffect(Entity entity) {
        if (entity.getEntityWorld().isClient()) {
            return;
        }
        ServerWorld world = (ServerWorld) entity.getEntityWorld();
        BlockPos blockPos = entity.getBlockPos();

        world.playSound(null, blockPos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.PLAYERS, 1.0f, 1.0f);
        world.playSound(null, blockPos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.5f, 1.0f);

        world.spawnParticles(ParticleTypes.SOUL, entity.getX(), entity.getY(), entity.getZ(), 150, .05, 0.05, 0.05, .05);

    }
}
