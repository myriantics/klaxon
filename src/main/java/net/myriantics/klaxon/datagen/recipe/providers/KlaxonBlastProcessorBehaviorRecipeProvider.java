package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.api.NamedIngredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.custom.KlaxonBlastProcessorCatalystBehaviors;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class KlaxonBlastProcessorBehaviorRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonBlastProcessorBehaviorRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildBlastProcessorBehaviorRecipes();
    }

    private void buildBlastProcessorBehaviorRecipes() {
        addBlastProcessorBehaviorRecipe(NamedIngredient.ofItems(Items.FIREWORK_ROCKET), KlaxonBlastProcessorCatalystBehaviors.FIREWORK_ROCKET_ID);
        addBlastProcessorBehaviorRecipe(NamedIngredient.ofItems(Items.FIREWORK_STAR), KlaxonBlastProcessorCatalystBehaviors.FIREWORK_STAR_ID);
        addBlastProcessorBehaviorRecipe(NamedIngredient.fromTag(KlaxonItemTags.BEDLIKE_EXPLODABLES), KlaxonBlastProcessorCatalystBehaviors.BEDLIKE_EXPLODABLE_ID);
        addBlastProcessorBehaviorRecipe(NamedIngredient.ofItems(Items.WIND_CHARGE), KlaxonBlastProcessorCatalystBehaviors.WIND_CHARGE_ID);
        addBlastProcessorBehaviorRecipe(NamedIngredient.ofItems(Items.DRAGON_BREATH), KlaxonBlastProcessorCatalystBehaviors.DRAGONS_BREATH_ID);
        addBlastProcessorBehaviorRecipe(NamedIngredient.ofItems(Items.GLOWSTONE_DUST), KlaxonBlastProcessorCatalystBehaviors.GLOWSTONE_DUST_ID);
        addBlastProcessorBehaviorRecipe(NamedIngredient.ofItems(Items.GLOWSTONE), KlaxonBlastProcessorCatalystBehaviors.GLOWSTONE_BLOCK_ID);
    }
}
