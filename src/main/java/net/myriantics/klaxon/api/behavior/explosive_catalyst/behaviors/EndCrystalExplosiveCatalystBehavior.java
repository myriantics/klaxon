package net.myriantics.klaxon.api.behavior.explosive_catalyst.behaviors;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class EndCrystalExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public EndCrystalExplosiveCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData data) {
        BlockState blastProcessorState = world.getBlockState(pos);
        Direction facing = blastProcessorState.get(Properties.HORIZONTAL_FACING);

        // check to see if bedrock is under, in front of, or below the output position of blast processor
        boolean fiery = data.producesFire() || isStateValid(world, pos.down()) || isStateValid(world, pos.offset(facing)) || isStateValid(world, pos.offset(facing).down());

        return new ExplosiveCatalystData(this, data.explosionPower(), data.producesFire() || fiery);
    }

    private boolean isStateValid(World world, BlockPos pos) {
        return world.getBlockState(pos).isOf(Blocks.BEDROCK);
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
