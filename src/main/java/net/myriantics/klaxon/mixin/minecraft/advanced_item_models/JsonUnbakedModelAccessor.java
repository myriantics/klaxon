package net.myriantics.klaxon.mixin.minecraft.advanced_item_models;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(JsonUnbakedModel.class)
public interface JsonUnbakedModelAccessor {

    @Accessor(value = "textureMap")
    Map<String, Either<SpriteIdentifier, String>> klaxon$getTextureMap();

    @Accessor(value = "parentId")
    @Nullable Identifier klaxon$getParentId();
}
