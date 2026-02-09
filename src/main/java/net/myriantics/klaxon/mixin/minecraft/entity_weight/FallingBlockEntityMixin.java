package net.myriantics.klaxon.mixin.minecraft.entity_weight;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.myriantics.klaxon.mechanics.entity_weight.AdvancedEntityWeightBehavior;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin implements AdvancedEntityWeightBehavior {
    @Shadow
    public abstract BlockState getBlockState();

    @Unique
    @Override
    public boolean klaxon$isHeavy() {
        return this.getBlockState().isIn(KlaxonBlockTags.HEAVY_FALLING_BLOCKS);
    }
}
