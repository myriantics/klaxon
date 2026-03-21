package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public class KlaxonManualItemApplicationRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonManualItemApplicationRecipeProvider(KlaxonRecipeProvider provider, RecipeOutput exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        generateManualItemApplicationRecipes();
    }

    private void generateManualItemApplicationRecipes() {
        addManualItemApplicationRecipe(
                KlaxonBlockTags.NETHER_REACTOR_CORE_CONVERTIBLE,
                Ingredient.of(KlaxonItems.HALLNOX_POD),
                KlaxonBlocks.NETHER_REACTOR_CORE
        );
        addManualItemApplicationRecipe(
                KlaxonBlockTags.CRUDE_NETHER_REACTOR_CORE_CONVERTIBLE,
                Ingredient.of(KlaxonItems.HALLNOX_POD),
                KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE
        );
    }
}
