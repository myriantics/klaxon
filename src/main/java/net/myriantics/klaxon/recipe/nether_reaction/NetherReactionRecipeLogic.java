package net.myriantics.klaxon.recipe.nether_reaction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

import java.util.List;

public abstract class NetherReactionRecipeLogic {

    public static boolean test(BlockState explosionOriginState) {
        return explosionOriginState.is(KlaxonBlockTags.NETHER_REACTOR_CORES);
    }

    /**
     * @param targetState
     * The state currently being processed by the explosion.
     * @param targetPos
     * The BlockPos currently being processed by the explosion
     * @param serverWorld
     * The server world. This is only run on the server.
     * @return
     * The output BlockState. This is immediately set into the world.
     */
    public static BlockState getOutputState(
            BlockState targetState,
            BlockPos targetPos,
            ServerLevel serverWorld,
            Explosion explosion
    ) {
        List<RecipeHolder<NetherReactionRecipe>> matches = serverWorld.getRecipeManager().getRecipesFor(KlaxonRecipeTypes.NETHER_REACTION.value(), new NetherReactionRecipeInput(targetState), serverWorld);

        Vec3 center = Vec3.atCenterOf(targetPos);
        BlockHitResult hitResult = new BlockHitResult(center, Direction.getNearest(explosion.center().subtract(center)), targetPos, false);

        BlockState newState = null;
        Block checkedBlock = null;

        // iterate through all the valid recipes to find any valid state
        for (RecipeHolder<NetherReactionRecipe> match : matches) {
            Block resultBlock = match.value().getOutputBlock();
            checkedBlock = resultBlock;

            // preserve properties of original state if applicable
            try {
                newState = resultBlock.getStateForPlacement(new BlockPlaceContext(serverWorld, null, null, new ItemStack(resultBlock), hitResult));
            } catch (NullPointerException e) {
                continue;
            }

            // if that method didn't give us a valid state at this point, clear state and skip to next entry
            if (newState == null || !newState.canSurvive(serverWorld, targetPos)) {
                newState = null;
                continue;
            }

            // copy all valid properties from target state onto new state
            for (Property<?> property : targetState.getBlock().getStateDefinition().getProperties()) {
                if (newState.hasProperty(property)) newState = copyProperty(targetState, newState, property);
            }

            // override to prevent mushroom stems being malformed when converting to stems - defaults to vertical
            if (targetState.getBlock() instanceof HugeMushroomBlock && newState.hasProperty(BlockStateProperties.AXIS)) {
                if (!targetState.getValue(HugeMushroomBlock.UP) && !targetState.getValue(HugeMushroomBlock.DOWN)) {
                    newState = newState.setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
                } else if (!targetState.getValue(HugeMushroomBlock.EAST) && !targetState.getValue(HugeMushroomBlock.WEST)) {
                    newState = newState.setValue(BlockStateProperties.AXIS, Direction.Axis.X);
                } else if (!targetState.getValue(HugeMushroomBlock.NORTH) && !targetState.getValue(HugeMushroomBlock.SOUTH)) {
                    newState = newState.setValue(BlockStateProperties.AXIS, Direction.Axis.Z);
                }
            }

            // makes mangrove trees & converted cactus patches look better
            if (newState.hasProperty(BlockStateProperties.AXIS) && (targetState.is(Blocks.MANGROVE_ROOTS) || targetState.is(Blocks.MANGROVE_ROOTS) || targetState.is(Blocks.CACTUS))) newState = newState.setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
        }

        // if we still don't have a state to place, use the block's default state while trying to preserve properties.
        if (newState == null && checkedBlock != null) newState = checkedBlock.withPropertiesOf(targetState);

        // make sure we have a state to set before we set it.
        if (newState != null) {
            // make sure we can place the block before spawning it - this prevents items getting dropped and wack shit happening
            if (newState.canSurvive(serverWorld, targetPos)) {
                return newState;
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }

        // if we couldn't find a match, don't change the blockstate.
        // if we did, but none were valid, overwrite with air.
        return matches.isEmpty() ? targetState : Blocks.AIR.defaultBlockState();
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState source, BlockState target, Property<T> property) {
        return target.setValue(property, source.getValue(property));
    }
}
