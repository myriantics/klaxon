package net.myriantics.klaxon.mixin.minecraft.datagen;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(Model.class)
public interface ModelAccessor {

    @Accessor("requiredTextures")
    public Set<TextureKey> klaxon$getRequiredTextures();
}
