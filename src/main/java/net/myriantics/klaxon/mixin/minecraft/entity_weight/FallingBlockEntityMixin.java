package net.myriantics.klaxon.mixin.minecraft.entity_weight;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
        return this.getBlockState().is(KlaxonBlockTags.HEAVY_FALLING_BLOCKS);
    }
}
