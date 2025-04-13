package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.datagen.KlaxonDatagenPhantomItems;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.List;

public class KlaxonBlastProcessingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonBlastProcessingRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildBlastProcessingRecipes();
    }

    private void buildBlastProcessingRecipes() {
        addBlastProcessingRecipe(Ingredient.ofItems(Items.DEEPSLATE_BRICKS), 0.1, 0.3, new ItemStack(Items.CRACKED_DEEPSLATE_BRICKS));
        addBlastProcessingRecipe(Ingredient.ofItems(Items.DEEPSLATE_TILES), 0.1, 0.3, new ItemStack(Items.CRACKED_DEEPSLATE_TILES));
        addBlastProcessingRecipe(Ingredient.ofItems(Items.NETHER_BRICKS), 0.1, 0.3, new ItemStack(Items.CRACKED_NETHER_BRICKS));
        addBlastProcessingRecipe(Ingredient.ofItems(Items.POLISHED_BLACKSTONE_BRICKS), 0.1, 0.3, new ItemStack(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS));
        addBlastProcessingRecipe(Ingredient.ofItems(Items.STONE_BRICKS), 0.1, 0.3, new ItemStack(Items.CRACKED_STONE_BRICKS));
        addBlastProcessingRecipe(Ingredient.ofItems(Items.COAL), 0.1, 0.3, new ItemStack(KlaxonItems.FRACTURED_COAL_CHUNKS));
        addBlastProcessingRecipe(Ingredient.fromTag(KlaxonItemTags.OVERWORLD_RUBBER_EXTRACTABLE_LOGS), 0.5, 1.0, new ItemStack(KlaxonItems.RUBBER_GLOB));
        addBlastProcessingRecipe(Ingredient.fromTag(KlaxonItemTags.NETHER_RUBBER_EXTRACTABLE_LOGS), 0.8, 1.5, new ItemStack(KlaxonItems.MOLTEN_RUBBER_GLOB, 3));

        // create compat
        addBlastProcessingRecipe(Ingredient.ofItems(KlaxonDatagenPhantomItems.CREATE_PRECISION_MECHANISM), 0.2, 0.4, new ItemStack(Items.CLOCK),
                new AllModsLoadedResourceCondition(List.of(KlaxonDatagenPhantomItems.CREATE_MOD_ID)));
    }
}
