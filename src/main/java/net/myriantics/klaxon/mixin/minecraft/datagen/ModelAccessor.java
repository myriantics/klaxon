package net.myriantics.klaxon.mixin.minecraft.datagen;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;
import java.util.Set;

@Mixin(Model.class)
public interface ModelAccessor {

    @Accessor("requiredTextures")
    public Set<TextureKey> klaxon$getRequiredTextures();

    @Accessor("parent")
    public Optional<Identifier> klaxon$getParent();
}
