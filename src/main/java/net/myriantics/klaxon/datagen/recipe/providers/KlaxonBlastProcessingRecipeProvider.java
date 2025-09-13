package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.myriantics.klaxon.api.NamedIngredient;
import net.myriantics.klaxon.api.RecipeOutputCompound;
import net.myriantics.klaxon.datagen.KlaxonDatagenCompatIds;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.List;

public class KlaxonBlastProcessingRecipeProvider extends KlaxonRecipeSubProvider {

    public KlaxonBlastProcessingRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildBlastProcessingRecipes();
        buildExplosiveDisassemblyRecipes();
    }

    private void buildBlastProcessingRecipes() {
        // brick cracking
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.DEEPSLATE_BRICKS), 0.4, 0.9, new ItemStack(Items.CRACKED_DEEPSLATE_BRICKS));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.DEEPSLATE_TILES), 0.4, 0.9, new ItemStack(Items.CRACKED_DEEPSLATE_TILES));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.NETHER_BRICKS), 0.3, 0.8, new ItemStack(Items.CRACKED_NETHER_BRICKS));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.POLISHED_BLACKSTONE_BRICKS), 0.3, 0.8, new ItemStack(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.STONE_BRICKS), 0.3, 0.8, new ItemStack(Items.CRACKED_STONE_BRICKS));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.INFESTED_STONE_BRICKS), 0.1, 0.3, new ItemStack(Items.INFESTED_CRACKED_STONE_BRICKS));

        // misc
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.COAL), 0.3, 1.4, RecipeOutputCompound.of(KlaxonItems.FRACTURED_COAL, 1.0, KlaxonItems.FRACTURED_COAL, 0.5));
        addBlastProcessingRecipe(NamedIngredient.ofItems(Items.CHARCOAL), 0.2, 1.2, RecipeOutputCompound.of(KlaxonItems.FRACTURED_CHARCOAL, 1.0, KlaxonItems.FRACTURED_CHARCOAL, 0.4));

        // rubber extraction
        addBlastProcessingRecipe(NamedIngredient.fromTag(KlaxonItemTags.OVERWORLD_RUBBER_EXTRACTABLE_LOGS), 0.8, 1.9, new ItemStack(KlaxonItems.RUBBER_GLOB, 5));
        addBlastProcessingRecipe(NamedIngredient.fromTag(KlaxonItemTags.NETHER_RUBBER_EXTRACTABLE_LOGS), 1.2, 2.5, new ItemStack(KlaxonItems.MOLTEN_RUBBER_GLOB, 5));
    }

    private void buildExplosiveDisassemblyRecipes() {
        // casings
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(KlaxonItems.STEEL_CASING),
                1.5,
                4.0,
                builder -> builder
                        .chance(KlaxonItems.STEEL_INGOT, 0.8)
                        .chance(KlaxonItems.STEEL_PLATE, 0.8)
                        .chance(KlaxonItems.STEEL_NUGGET, 4, 0.4)
        );
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(KlaxonItems.CRUDE_STEEL_CASING),
                0.8,
                2.4,
                builder -> builder
                        .chance(KlaxonItems.CRUDE_STEEL_INGOT, 0.6)
                        .chance(KlaxonItems.CRUDE_STEEL_PLATE, 0.6)
                        .chance(KlaxonItems.CRUDE_STEEL_NUGGET, 4, 0.3)
        );

        // nether reactor cores
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(KlaxonItems.NETHER_REACTOR_CORE),
                1.5,
                4.0,
                builder -> builder
                        .chance(KlaxonItems.STEEL_INGOT, 0.8)
                        .chance(KlaxonItems.STEEL_PLATE, 0.8)
                        .chance(KlaxonItems.HALLNOX_POD, 0.5)
                        .chance(KlaxonItems.STEEL_NUGGET, 4, 0.4)
        );
        addExplosiveDisassemblyRecipe(
                NamedIngredient.ofItems(KlaxonItems.CRUDE_NETHER_REACTOR_CORE),
                0.8,
                2.4,
                builder -> builder
                        .chance(KlaxonItems.CRUDE_STEEL_INGOT, 0.6)
                        .chance(KlaxonItems.CRUDE_STEEL_PLATE, 0.6)
                        .chance(KlaxonItems.HALLNOX_POD, 0.4)
                        .chance(KlaxonItems.CRUDE_STEEL_NUGGET, 4, 0.3)
        );
    }
}
