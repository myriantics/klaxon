package net.myriantics.klaxon.mixin.minecraft.nether_reaction;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionConversionListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BeehiveBlockEntity.class)
public abstract class BeehiveBlockEntityMixin implements NetherReactionConversionListener {
    @Shadow
    public abstract void emptyAllLivingFromHive(@Nullable Player player, BlockState state, BeehiveBlockEntity.BeeReleaseStatus beeState);

    @Override
    public void klaxon$beforeConversion(BlockState oldState, BlockState newState) {
        this.emptyAllLivingFromHive(null, oldState, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
    }
}
