package net.myriantics.klaxon.util;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipes.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;

import java.util.Optional;

public abstract class ItemExplosionPowerHelper {

    public static Optional<ItemExplosionPowerRecipe> getExplosionPowerData(World world, ItemStack itemStack) {
        if (world == null) {
            return Optional.empty();
        }

        RecipeManager recipeManager = world.getRecipeManager();

        RecipeType<ItemExplosionPowerRecipe> type = KlaxonRecipeTypes.ITEM_EXPLOSION_POWER;
        // yay this will make things gooder
        SimpleInventory simpleInventory = new SimpleInventory(2);
        simpleInventory.setStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX, itemStack.copy());

        return recipeManager.getFirstMatch(type, simpleInventory, world);
    }

    public static double getItemExplosionPower(World world, ItemStack itemStack) {
        Optional<ItemExplosionPowerRecipe> match = getExplosionPowerData(world, itemStack);
        if (match.isEmpty()) {
            return 0.0F;
        }
        return match.get().getExplosionPower();
    }

    public static boolean isValidCatalyst(World world, ItemStack stack) {
        if (world == null) {
            return false;
        }
        Optional<ItemExplosionPowerRecipe> match = ItemExplosionPowerHelper.getExplosionPowerData(world, stack);

        return match.isPresent() && match.get().getExplosionPower() > 0;
    }
}
