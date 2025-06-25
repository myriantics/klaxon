package net.myriantics.klaxon.recipe.explosion_conversion;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    public static BlockState getOutputState(BlockState conversionCatalystState, BlockState targetState, BlockPos targetPos, ServerWorld serverWorld) {
        Optional<RecipeEntry<ExplosionConversionRecipe>> match = serverWorld.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.EXPLOSION_CONVERSION, new ExplosionConversionRecipeInput(conversionCatalystState, targetState), serverWorld);

        if (match.isPresent()) {
            // preserve properties of original state if applicable
            return match.get().value().getOutputBlock().getStateWithProperties(targetState);
        }

        // if we couldn't find a match, don't change the blockstate.
        return targetState;
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
}
