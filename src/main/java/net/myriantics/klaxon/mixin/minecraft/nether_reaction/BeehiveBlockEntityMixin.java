package net.myriantics.klaxon.mixin.minecraft.nether_reaction;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionConversionListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BeehiveBlockEntity.class)
public abstract class BeehiveBlockEntityMixin implements NetherReactionConversionListener {
    @Shadow
    public abstract void angerBees(@Nullable PlayerEntity player, BlockState state, BeehiveBlockEntity.BeeState beeState);

    @Override
    public void klaxon$beforeConversion(BlockState oldState, BlockState newState) {
        this.angerBees(null, oldState, BeehiveBlockEntity.BeeState.EMERGENCY);
    }
}
