package net.myriantics.klaxon.recipe.cooling;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

// Inspiration taken from AE2's Item Transformation system
public abstract class ItemCoolingRecipeLogic {
    private static Set<Item> COOLABLE_ITEM_CACHE = new HashSet<>();

    public static boolean test(World world, ItemStack stack) {
        return getCoolableItems(world).contains(stack.getItem());
    }

    private static Set<Item> getCoolableItems(World world) {
        if (COOLABLE_ITEM_CACHE.isEmpty()) {

            Set<Item> newCache = new HashSet<>();
            for (RecipeEntry<ItemCoolingRecipe> entry : world.getRecipeManager().listAllOfType(KlaxonRecipeTypes.ITEM_COOLING)) {
                // add all the compatible items to the new cache
                for (ItemStack stack : entry.value().getInputIngredient().getMatchingStacks()) {
                    newCache.add(stack.getItem());
                }
            }

            // update stored cache
            COOLABLE_ITEM_CACHE = newCache;
            return newCache;
        } else {
            return COOLABLE_ITEM_CACHE;
        }
    }

    public static Optional<ItemStack> getCooledStack(World world, ItemStack inputStack) {
        Optional<ItemStack> output = Optional.empty();

        RecipeInput input = new RecipeInput() {
            @Override
            public ItemStack getStackInSlot(int slot) {
                return inputStack;
            }

            @Override
            public int getSize() {
                return 1;
            }
        };

        Optional<RecipeEntry<ItemCoolingRecipe>> match =
                world.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.ITEM_COOLING, input, world);

        // if we have a recipe match, overwrite the output
        if (match.isPresent()) {
            output = Optional.of(match.get().value().craft(input, world.getRegistryManager()));
        }

        return output;
    }

    private static void clearCache() {
        COOLABLE_ITEM_CACHE.clear();
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
