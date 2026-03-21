package net.myriantics.klaxon.mixin.minecraft.advanced_item_models;

import com.mojang.datafixers.util.Either;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

@Mixin(BlockModel.class)
public interface BlockModelAccessor {

    @Accessor(value = "textureMap")
    Map<String, Either<Material, String>> klaxon$getTextureMap();

    @Accessor(value = "parentLocation")
    @Nullable ResourceLocation klaxon$getParentId();
}
