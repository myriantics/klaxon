package net.myriantics.klaxon.mixin.hammer_model;

import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {

    // Used in ItemRendererMixin
    @Accessor("models")
    ItemModels klaxon$getModels();
}