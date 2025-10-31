package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.myriantics.klaxon.datagen.NamedIngredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.behavior.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class KlaxonExplosiveCatalystDefinitionRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonExplosiveCatalystDefinitionRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildItemExplosionPowerRecipes();
    }

    private void buildItemExplosionPowerRecipes() {
        // normie recipes
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.TNT), 4.0, false, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.GUNPOWDER), 0.8, false, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.BLAZE_POWDER), 0.5, true, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.FIRE_CHARGE), 1.3, true, false);

        // these have custom behaviors
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.CREEPER_HEAD), KlaxonExplosiveCatalystBehaviors.CREEPER_HEAD, 6.0, false, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.END_CRYSTAL), KlaxonExplosiveCatalystBehaviors.END_CRYSTAL, 6.0, false, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.TNT_MINECART), KlaxonExplosiveCatalystBehaviors.TNT_MINECART, 5.0, false, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.GLOWSTONE_DUST), KlaxonExplosiveCatalystBehaviors.GLOWSTONE, 1.3, true, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.GLOWSTONE), KlaxonExplosiveCatalystBehaviors.GLOWSTONE, 5.0, true, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.DRAGON_BREATH), KlaxonExplosiveCatalystBehaviors.DRAGONS_BREATH, 2.5, false, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.WIND_CHARGE), KlaxonExplosiveCatalystBehaviors.WIND_CHARGE, 0.0, false, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.fromTag(KlaxonItemTags.BEDLIKE_EXPLODABLES), KlaxonExplosiveCatalystBehaviors.BEDLIKE_EXPLODABLE, 5.0, true, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.FIREWORK_ROCKET), KlaxonExplosiveCatalystBehaviors.FIREWORK_ROCKET, 0.0, false, false);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.FIREWORK_STAR), KlaxonExplosiveCatalystBehaviors.FIREWORK_STAR, 0.8, false, false);

        // meme recipes
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.CREEPER_SPAWN_EGG), 3.0, false, true);
        addExplosiveCatalystDefinitionRecipe(NamedIngredient.ofItems(Items.GHAST_SPAWN_EGG), 3.5, true, true);
    }
}
