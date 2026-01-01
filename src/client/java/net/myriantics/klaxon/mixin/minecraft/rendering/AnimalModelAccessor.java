package net.myriantics.klaxon.mixin.minecraft.rendering;

import net.minecraft.client.render.entity.model.AnimalModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnimalModel.class)
public interface AnimalModelAccessor {
    @Accessor("headScaled")
    boolean klaxon$headScaled();

    @Accessor("childHeadYOffset")
    float klaxon$childHeadYOffset();

    @Accessor("childHeadZOffset")
    float klaxon$childHeadZOffset();

    @Accessor("invertedChildHeadScale")
    float klaxon$invertedChildHeadScale();
}
