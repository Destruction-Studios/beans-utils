package net.ds.mixin;

import net.ds.combatLog.CombatData;
import net.ds.interfaces.IEntityDataSaver;
import net.ds.petRespawning.PetData;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements IEntityDataSaver {
    @Unique
    private NbtCompound persistentCombatData;
    @Unique
    private NbtCompound persistentPetData;

    @Inject(method = "writeData", at = @At("HEAD"))
    public void writeDataMixin(WriteView view, CallbackInfo ci) {
        if (persistentCombatData != null) {
            view.put(CombatData.COMBAT_LOG_NBT_KEY, NbtCompound.CODEC, persistentCombatData);
        }
        if (persistentPetData != null) {
            view.put(PetData.PET_NBT_KEY, NbtCompound.CODEC, persistentPetData);
        }
    }

    @Inject(method="readData", at = @At("HEAD"))
    public void readDataMixin(ReadView view, CallbackInfo ci) {
        persistentPetData = view.read(PetData.PET_NBT_KEY, NbtCompound.CODEC).orElse(null);
        persistentCombatData = view.read(CombatData.COMBAT_LOG_NBT_KEY, NbtCompound.CODEC).orElse(null);
    }

    @Override
    public NbtCompound beans_utils$persistentCombatData() {
        if (this.persistentCombatData == null) {
            this.persistentCombatData = new NbtCompound();
        }
        return persistentCombatData;
    }

    @Override
    public NbtCompound beans_utils$persistentPetData() {
        if (this.persistentPetData == null) {
            this.persistentPetData = new NbtCompound();
        }
        return persistentPetData;
    }
}
