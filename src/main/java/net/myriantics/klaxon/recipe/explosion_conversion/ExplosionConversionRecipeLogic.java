package net.myriantics.klaxon.recipe.explosion_conversion;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;

import java.util.*;

// Inspired by AE2's Item Transformation System
public abstract class ExplosionConversionRecipeLogic {
    private static Set<Block> VALID_CONVERSION_CATALYSTS_CACHE = new HashSet<>();

    public static boolean test(World world, BlockState explosionOriginState) {
        return getValidConversionCatalysts(world).contains(explosionOriginState.getBlock());
    }

    private static Set<Block> getValidConversionCatalysts(World world) {
        if (VALID_CONVERSION_CATALYSTS_CACHE.isEmpty()) {

            Set<Block> newCache = new HashSet<>();
            for (RecipeEntry<ExplosionConversionRecipe> entry : world.getRecipeManager().listAllOfType(KlaxonRecipeTypes.EXPLOSION_CONVERSION)) {

                for (RegistryEntry<Block> block : Registries.BLOCK.iterateEntries(entry.value().getValidConversionCatalysts())) {
                    newCache.add(block.value());
                }
            }

            // update stored cache
            VALID_CONVERSION_CATALYSTS_CACHE = newCache;
            return newCache;
        } else {
            return VALID_CONVERSION_CATALYSTS_CACHE;
        }
    }

    /**
     *
     * @param conversionCatalystState
     * The state at the explosion's origin - already vetted to be a valid conversion catalyst.
     * @param targetState
     * The state currently being processed by the explosion.
     * @param targetPos
     * The BlockPos currently being processed by the explosion
     * @param serverWorld
     * The server world. This is only run on the server.
     * @return
     * The output BlockState. This is immediately set into the world.
     */
    public static BlockState getOutputState(BlockState conversionCatalystState, BlockState targetState, BlockPos targetPos, ServerWorld serverWorld, Explosion explosion) {

        List<RecipeEntry<ExplosionConversionRecipe>> matches = serverWorld.getRecipeManager().getAllMatches(KlaxonRecipeTypes.EXPLOSION_CONVERSION, new ExplosionConversionRecipeInput(conversionCatalystState, targetState), serverWorld);

        Vec3d center = Vec3d.ofCenter(targetPos);
        BlockHitResult hitResult = new BlockHitResult(center, Direction.getFacing(explosion.getPosition().subtract(center)), targetPos, false);

        BlockState newState = null;
        Block checkedBlock = null;

        // iterate through all the valid recipes to find any valid state
        for (RecipeEntry<ExplosionConversionRecipe> match : matches) {
            Block resultBlock = match.value().getOutputBlock();
            checkedBlock = resultBlock;

            // preserve properties of original state if applicable
            try {
                newState = resultBlock.getPlacementState(new ItemPlacementContext(serverWorld, null, null, new ItemStack(resultBlock), hitResult));
            } catch (NullPointerException e) {
                continue;
            }

            // if that method didn't give us a valid state at this point, clear state and skip to next entry
            if (newState == null || !newState.canPlaceAt(serverWorld, targetPos)) {
                newState = null;
                continue;
            }

            // copy all valid properties from target state onto new state
            for (Property<?> property : targetState.getBlock().getStateManager().getProperties()) {
                if (newState.contains(property)) newState = copyProperty(targetState, newState, property);
            }

            // override to prevent mushroom stems being malformed when converting to stems - defaults to vertical
            if (targetState.getBlock() instanceof MushroomBlock && newState.contains(Properties.AXIS)) {
                if (!targetState.get(MushroomBlock.UP) && !targetState.get(MushroomBlock.DOWN)) {
                    newState = newState.with(Properties.AXIS, Direction.Axis.Y);
                } else if (!targetState.get(MushroomBlock.EAST) && !targetState.get(MushroomBlock.WEST)) {
                    newState = newState.with(Properties.AXIS, Direction.Axis.X);
                } else if (!targetState.get(MushroomBlock.NORTH) && !targetState.get(MushroomBlock.SOUTH)) {
                    newState = newState.with(Properties.AXIS, Direction.Axis.Z);
                }
            }

            // makes mangrove trees & converted cactus patches look better
            if (newState.contains(Properties.AXIS) && (targetState.isOf(Blocks.MANGROVE_ROOTS) || targetState.isOf(Blocks.MANGROVE_ROOTS) || targetState.isOf(Blocks.CACTUS))) newState = newState.with(Properties.AXIS, Direction.Axis.Y);
        }

        // if we still don't have a state to place, use the block's default state while trying to preserve properties.
        if (newState == null && checkedBlock != null) newState = checkedBlock.getStateWithProperties(targetState);

        // make sure we have a state to set before we set it.
        if (newState != null) {
            // make sure we can place the block before spawning it - this prevents items getting dropped and wack shit happening
            if (newState.canPlaceAt(serverWorld, targetPos)) {
                return newState;
            } else {
                return Blocks.AIR.getDefaultState();
            }
        }

        // if we couldn't find a match, don't change the blockstate.
        // if we did, but none were valid, overwrite with air.
        return matches.isEmpty() ? targetState : Blocks.AIR.getDefaultState();
    }


    private static void clearCache() {
        VALID_CONVERSION_CATALYSTS_CACHE.clear();
    }

    public static void onServerStarted(MinecraftServer minecraftServer) {
        clearCache();
    }

    public static void onDatapackReload(MinecraftServer minecraftServer, LifecycledResourceManager lifecycledResourceManager, boolean success) {
        if (success) clearCache();
    }

    public static void onTagsLoaded(DynamicRegistryManager registryManager, boolean success) {
        if (success) clearCache();
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState source, BlockState target, Property<T> property) {
        return target.with(property, source.get(property));
    }
}
