package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

public class EndCrystalBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {
    public EndCrystalBlastProcessorCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystRecipeInput recipeInventory) {
        ItemExplosionPowerData base = super.getExplosionPowerData(world, pos, blastProcessor, recipeInventory);
        BlockState blastProcessorState = world.getBlockState(pos);
        Direction facing = blastProcessorState.get(Properties.HORIZONTAL_FACING);

        // check to see if bedrock is under, in front of, or below the output position of blast processor
        boolean fiery = base.producesFire() || isStateValid(world, pos.down()) || isStateValid(world, pos.offset(facing)) || isStateValid(world, pos.offset(facing).down());

        return new ItemExplosionPowerData(base.explosionPower(), base.producesFire() || fiery);
    }

    private boolean isStateValid(World world, BlockPos pos) {
        return world.getBlockState(pos).isOf(Blocks.BEDROCK);
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
