package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.KlaxonDatagenPhantomItems;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.KlaxonItems;

import java.util.List;
import java.util.function.Consumer;

public class KlaxonBlastProcessingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonBlastProcessingRecipeProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> consumer) {
        buildBlastProcessingRecipes(consumer);
    }

    private void buildBlastProcessingRecipes(Consumer<RecipeJsonProvider> consumer) {
        addBlastProcessingRecipe(consumer, Ingredient.ofItems(Items.DEEPSLATE_BRICKS), 0.1, 0.3, new ItemStack(Items.CRACKED_DEEPSLATE_BRICKS));
        addBlastProcessingRecipe(consumer, Ingredient.ofItems(Items.DEEPSLATE_TILES), 0.1, 0.3, new ItemStack(Items.CRACKED_DEEPSLATE_TILES));
        addBlastProcessingRecipe(consumer, Ingredient.ofItems(Items.NETHER_BRICKS), 0.1, 0.3, new ItemStack(Items.CRACKED_NETHER_BRICKS));
        addBlastProcessingRecipe(consumer, Ingredient.ofItems(Items.POLISHED_BLACKSTONE_BRICKS), 0.1, 0.3, new ItemStack(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS));
        addBlastProcessingRecipe(consumer, Ingredient.ofItems(Items.STONE_BRICKS), 0.1, 0.3, new ItemStack(Items.CRACKED_STONE_BRICKS));
        addBlastProcessingRecipe(consumer, Ingredient.ofItems(Items.COAL), 0.1, 0.3, new ItemStack(KlaxonItems.FRACTURED_COAL_CHUNKS));

        // create compat
        addBlastProcessingRecipe(consumer, Ingredient.ofItems(KlaxonDatagenPhantomItems.CREATE_PRECISION_MECHANISM), 0.2, 0.4, new ItemStack(Items.CLOCK),
                DefaultResourceConditions.allModsLoaded(KlaxonDatagenPhantomItems.CREATE_MOD_ID));
    }
}
