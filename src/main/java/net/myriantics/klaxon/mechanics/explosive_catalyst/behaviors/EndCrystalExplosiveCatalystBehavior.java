package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.myriantics.klaxon.mechanics.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class EndCrystalExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public EndCrystalExplosiveCatalystBehavior(ResourceLocation id) {
        super(id);
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData data) {
        BlockState blastProcessorState = world.getBlockState(pos);
        Direction facing = blastProcessorState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        // check to see if bedrock is under, in front of, or below the output position of blast processor
        boolean fiery = data.producesFire() || isStateValid(world, pos.below()) || isStateValid(world, pos.relative(facing)) || isStateValid(world, pos.relative(facing).below());

        return new ExplosiveCatalystData(this, data.explosionPower(), data.producesFire() || fiery);
    }

    private boolean isStateValid(Level world, BlockPos pos) {
        return world.getBlockState(pos).is(Blocks.BEDROCK);
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
