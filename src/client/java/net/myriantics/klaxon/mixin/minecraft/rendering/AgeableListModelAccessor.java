package net.myriantics.klaxon.mixin.minecraft.rendering;

import net.minecraft.client.model.AgeableListModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AgeableListModel.class)
public interface AgeableListModelAccessor {
    @Accessor("scaleHead")
    boolean klaxon$headScaled();

    @Accessor("babyYHeadOffset")
    float klaxon$childHeadYOffset();

    @Accessor("babyZHeadOffset")
    float klaxon$childHeadZOffset();

    @Accessor("babyHeadScale")
    float klaxon$invertedChildHeadScale();
}
