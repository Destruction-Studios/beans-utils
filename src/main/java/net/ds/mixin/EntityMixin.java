package net.ds.mixin;

import net.ds.combatLog.CombatData;
import net.ds.combatLog.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements IEntityDataSaver {
    private NbtCompound persistentData;

    @Inject(method = "writeData", at = @At("HEAD"))
    public void writeDataMixin(WriteView view, CallbackInfo ci) {
        if (persistentData != null) {
            view.put(CombatData.COMBAT_LOG_NBT_KEY, NbtCompound.CODEC, persistentData);
        }
    }

    @Inject(method="readData", at = @At("HEAD"))
    public void readDataMixin(ReadView view, CallbackInfo ci) {
        persistentData = view.read(CombatData.COMBAT_LOG_NBT_KEY, NbtCompound.CODEC).orElse(null);
    }

    @Override
    public NbtCompound getPersistentData() {
        if (this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        return persistentData;
    }
}
