package net.myriantics.klaxon.recipe.nether_reaction;

import net.minecraft.world.level.block.state.BlockState;

public interface NetherReactionConversionListener {
    void klaxon$beforeConversion(BlockState oldState, BlockState newState);
}
