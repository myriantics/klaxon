package net.myriantics.klaxon.recipe.world_item_application;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.registry.misc.KlaxonWorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class WorldItemApplicationRecipeLogic {
    private static Set<Item> APPLICABLE_ITEMS_CACHE = new HashSet<>();

    public static boolean test(Level world, ItemStack stack) {
        return getCoolableBlocks(world).contains(stack.getItem());
    }

    private static Set<Item> getCoolableBlocks(Level world) {
        if (APPLICABLE_ITEMS_CACHE.isEmpty()) {

            Set<Item> newCache = new HashSet<>();
            for (RecipeHolder<WorldItemApplicationRecipe> entry : world.getRecipeManager().getAllRecipesFor(KlaxonRecipeTypes.WORLD_ITEM_APPLICATION)) {
                // add all the compatible items to the new cache
                for (ItemStack stack : entry.value().getInputIngredient().getItems()) {
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

    public static Optional<BlockState> getResultState(Level world, WorldItemApplicationRecipeInput recipeInput) {
        Optional<BlockState> output = Optional.empty();

        Optional<RecipeHolder<WorldItemApplicationRecipe>> match =
                world.getRecipeManager().getRecipeFor(KlaxonRecipeTypes.WORLD_ITEM_APPLICATION, recipeInput, world);

        if (match.isPresent()) {
            // compatible properties are saved from input state
            output = Optional.of(match.get().value().getOutputBlock().withPropertiesOf(recipeInput.inputState()));
        }

        return output;
    }

    public static void affectWorld(ServerLevel serverWorld, BlockPos targetPos, BlockState newState, Direction clickDirection, @Nullable Player player, WorldItemApplicationRecipeInput recipeInput) {
        RandomSource random = serverWorld.getRandom();
        ItemStack usedStack = recipeInput.usedStack();
        BlockState targetState = recipeInput.inputState();

        // use place sound of used stack if possible
        // otherwise, use
        Block soundSourceBlock = targetState.getBlock();
        if (usedStack.getItem() instanceof BlockItem blockItem) soundSourceBlock = blockItem.getBlock();
        serverWorld.playSound(
                null,
                targetPos,
                soundSourceBlock.defaultBlockState().getSoundType().getPlaceSound(),
                SoundSource.BLOCKS,
                0.6f + (0.2f + random.nextFloat()),
                0.2f + (0.4f + random.nextFloat())
        );

        // apply modifications if possible
        if (newState.getBlock() instanceof WorldItemApplicationResult result) {
            newState = result.getResultState(serverWorld, newState, targetPos, clickDirection, player).orElse(newState);
        }

        // decrement stack and set the block state
        serverWorld.setBlockAndUpdate(targetPos, newState);
        KlaxonServerPlayNetworkHandler.syncWorldEvent(serverWorld, targetPos, KlaxonWorldEvents.SPAWN_BLOCK_BREAK_PARTICLES);
    }

    private static void clearCache() {
        APPLICABLE_ITEMS_CACHE.clear();
    }

    public static void onServerStarted(MinecraftServer minecraftServer) {
        clearCache();
    }

    public static void onDatapackReload(MinecraftServer minecraftServer, CloseableResourceManager lifecycledResourceManager, boolean success) {
        if (success) clearCache();
    }

    public static void onTagsLoaded(RegistryAccess registryManager, boolean success) {
        if (success) clearCache();
    }
}
