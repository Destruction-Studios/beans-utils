package net.ds.mixin.pets;

import net.ds.interfaces.IPetDataSaver;
import net.minecraft.entity.passive.CatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CatEntity.class)
public abstract class CatMixin implements IPetDataSaver {
    @Unique
    private boolean canRespawn = false;

    @Override
    public boolean beans_utils$getCanRespawn() {
        return canRespawn;
    }
    @Override
    public void beans_utils$setCanRespawn(boolean value) {
        this.canRespawn = value;
    }
}
