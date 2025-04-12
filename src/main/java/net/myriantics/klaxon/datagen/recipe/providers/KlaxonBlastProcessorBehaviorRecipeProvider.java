package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.custom.KlaxonBlastProcessorCatalystBehaviors;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class KlaxonBlastProcessorBehaviorRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonBlastProcessorBehaviorRecipeProvider(KlaxonRecipeProvider provider) {
        super(provider);
    }

    @Override
    public void generateRecipes(RecipeExporter exporter) {
        buildBlastProcessorBehaviorRecipes(exporter);
    }

    private void buildBlastProcessorBehaviorRecipes(RecipeExporter exporter) {
        addBlastProcessorBehaviorRecipe(exporter, Ingredient.ofItems(Items.FIREWORK_ROCKET), KlaxonBlastProcessorCatalystBehaviors.FIREWORK_ROCKET_ID);
        addBlastProcessorBehaviorRecipe(exporter, Ingredient.ofItems(Items.FIREWORK_STAR), KlaxonBlastProcessorCatalystBehaviors.FIREWORK_STAR_ID);
        addBlastProcessorBehaviorRecipe(exporter, Ingredient.fromTag(KlaxonItemTags.BEDLIKE_EXPLODABLES), KlaxonBlastProcessorCatalystBehaviors.BEDLIKE_EXPLODABLE_ID);
        addBlastProcessorBehaviorRecipe(exporter, Ingredient.ofItems(Items.WIND_CHARGE), KlaxonBlastProcessorCatalystBehaviors.WIND_CHARGE_ID);
        addBlastProcessorBehaviorRecipe(exporter, Ingredient.ofItems(Items.DRAGON_BREATH), KlaxonBlastProcessorCatalystBehaviors.DRAGONS_BREATH_ID);
        addBlastProcessorBehaviorRecipe(exporter, Ingredient.ofItems(Items.GLOWSTONE_DUST), KlaxonBlastProcessorCatalystBehaviors.GLOWSTONE_DUST_ID);
        addBlastProcessorBehaviorRecipe(exporter, Ingredient.ofItems(Items.GLOWSTONE), KlaxonBlastProcessorCatalystBehaviors.GLOWSTONE_BLOCK_ID);
    }
}
