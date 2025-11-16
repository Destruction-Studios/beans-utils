package net.ds.mixin.client;

import net.ds.interfaces.FilePackResource;
import net.minecraft.resource.ZipResourcePack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

@Mixin(ZipResourcePack.class)
public class ZipResourcePackMixin implements FilePackResource {
    @Final
    @Shadow
    private ZipResourcePack.ZipFileWrapper zipFile;

    @Override
    public File noload$getFile() {
        return zipFile.file;
    }
}
