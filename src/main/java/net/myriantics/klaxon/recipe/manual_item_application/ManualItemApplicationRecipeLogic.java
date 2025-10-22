package net.myriantics.klaxon.recipe.manual_item_application;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.ManualItemApplicationResult;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.registry.misc.KlaxonWorldEvents;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class ManualItemApplicationRecipeLogic {
    private static Set<Item> APPLICABLE_ITEMS_CACHE = new HashSet<>();

    public static boolean test(World world, ItemStack stack) {
        return getCoolableBlocks(world).contains(stack.getItem());
    }

    private static Set<Item> getCoolableBlocks(World world) {
        if (APPLICABLE_ITEMS_CACHE.isEmpty()) {

            Set<Item> newCache = new HashSet<>();
            for (RecipeEntry<WorldItemApplicationRecipe> entry : world.getRecipeManager().listAllOfType(KlaxonRecipeTypes.WORLD_ITEM_APPLICATION)) {
                // add all the compatible items to the new cache
                for (ItemStack stack : entry.value().getInputIngredient().getMatchingStacks()) {
                    newCache.add(stack.getItem());
                }
            }

            // update stored cache
            APPLICABLE_ITEMS_CACHE = newCache;
            return newCache;
        } else {
            return APPLICABLE_ITEMS_CACHE;
        }
    }

    public static Optional<BlockState> getResultState(World world, ManualItemApplicationRecipeInput recipeInput) {
        Optional<BlockState> output = Optional.empty();

        Optional<RecipeEntry<WorldItemApplicationRecipe>> match =
                world.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.WORLD_ITEM_APPLICATION, recipeInput, world);

        if (match.isPresent()) {
            // compatible properties are saved from input state
            output = Optional.of(match.get().value().getOutputBlock().getStateWithProperties(recipeInput.inputState()));
        }

        return output;
    }

    public static void affectWorld(ServerWorld serverWorld, BlockPos targetPos, BlockState newState, Direction clickDirection, @Nullable PlayerEntity player, ManualItemApplicationRecipeInput recipeInput) {
        Random random = serverWorld.getRandom();
        ItemStack usedStack = recipeInput.usedStack();
        BlockState targetState = recipeInput.inputState();

        // use place sound of used stack if possible
        // otherwise, use
        Block soundSourceBlock = targetState.getBlock();
        if (usedStack.getItem() instanceof BlockItem blockItem) soundSourceBlock = blockItem.getBlock();
        serverWorld.playSound(
                null,
                targetPos,
                soundSourceBlock.getDefaultState().getSoundGroup().getPlaceSound(),
                SoundCategory.BLOCKS,
                0.6f + (0.2f + random.nextFloat()),
                0.2f + (0.4f + random.nextFloat())
        );

        // apply modifications if possible
        if (newState.getBlock() instanceof ManualItemApplicationResult result) {
            newState = result.getResultState(serverWorld, newState, targetPos, clickDirection, player).orElse(newState);
        }

        // decrement stack and set the block state
        serverWorld.setBlockState(targetPos, newState);
        KlaxonServerPlayNetworkHandler.syncWorldEvent(serverWorld, targetPos, KlaxonWorldEvents.SPAWN_BLOCK_BREAK_PARTICLES);

    }

    private static void clearCache() {
        APPLICABLE_ITEMS_CACHE.clear();
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
