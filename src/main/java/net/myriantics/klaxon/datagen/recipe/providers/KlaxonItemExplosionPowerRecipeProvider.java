package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.myriantics.klaxon.api.NamedIngredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.behavior.KlaxonBlastProcessorCatalystBehaviors;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class KlaxonItemExplosionPowerRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonItemExplosionPowerRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildItemExplosionPowerRecipes();
    }

    private void buildItemExplosionPowerRecipes() {
        // normie recipes
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.TNT), 4.0, false, false);
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.GUNPOWDER), 0.8, false, false);
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.BLAZE_POWDER), 0.5, true, false);
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.FIRE_CHARGE), 1.3, true, false);

        // these have custom behaviors
        addItemExplosionPowerRecipeWithBehavior(NamedIngredient.ofItems(Items.CREEPER_HEAD), KlaxonBlastProcessorCatalystBehaviors.CREEPER_HEAD_ID, 6.0, false, false);
        addItemExplosionPowerRecipeWithBehavior(NamedIngredient.ofItems(Items.END_CRYSTAL), KlaxonBlastProcessorCatalystBehaviors.END_CRYSTAL_ID, 6.0, false, false);
        addItemExplosionPowerRecipeWithBehavior(NamedIngredient.ofItems(Items.TNT_MINECART), KlaxonBlastProcessorCatalystBehaviors.TNT_MINECART_ID, 5.0, false, false);
        addItemExplosionPowerRecipeWithBehavior(NamedIngredient.ofItems(Items.GLOWSTONE_DUST), KlaxonBlastProcessorCatalystBehaviors.GLOWSTONE_ID, 1.3, true, false);
        addItemExplosionPowerRecipeWithBehavior(NamedIngredient.ofItems(Blocks.GLOWSTONE), KlaxonBlastProcessorCatalystBehaviors.GLOWSTONE_ID, 5.0, true, false);
        addItemExplosionPowerRecipeWithBehavior(NamedIngredient.ofItems(Items.DRAGON_BREATH), KlaxonBlastProcessorCatalystBehaviors.DRAGONS_BREATH_ID, 2.5, false, false);
        addItemExplosionPowerRecipeWithBehavior(NamedIngredient.ofItems(Items.WIND_CHARGE), KlaxonBlastProcessorCatalystBehaviors.WIND_CHARGE_ID, 0.0, false, false);
        addItemExplosionPowerRecipeWithBehavior(NamedIngredient.fromTag(KlaxonItemTags.BEDLIKE_EXPLODABLES), KlaxonBlastProcessorCatalystBehaviors.BEDLIKE_EXPLODABLE_ID, 5.0, true, false);
        addItemExplosionPowerRecipeWithBehavior(NamedIngredient.ofItems(Items.FIREWORK_ROCKET), KlaxonBlastProcessorCatalystBehaviors.FIREWORK_ROCKET_ID, 0.0, false, false);
        addItemExplosionPowerRecipeWithBehavior(NamedIngredient.ofItems(Items.FIREWORK_STAR), KlaxonBlastProcessorCatalystBehaviors.FIREWORK_STAR_ID, 0.8, false, false);

        // meme recipes
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.CREEPER_SPAWN_EGG), 3.0, false, true);
        addItemExplosionPowerRecipe(NamedIngredient.ofItems(Items.GHAST_SPAWN_EGG), 3.5, true, true);
    }
}
