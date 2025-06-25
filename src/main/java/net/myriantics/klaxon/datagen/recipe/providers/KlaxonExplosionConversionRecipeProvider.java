package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.registry.tag.BlockTags;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;

public class KlaxonExplosionConversionRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonExplosionConversionRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildNetherReactionRecipes();
    }

    private void buildNetherReactionRecipes() {
        addNetherReactionRecipe(BlockTags.DIRT, Blocks.NETHERRACK);
        addNetherReactionRecipe(BlockTags.BASE_STONE_OVERWORLD, Blocks.NETHERRACK);
        addNetherReactionRecipe(ConventionalBlockTags.STONES, Blocks.NETHERRACK);
    }
}
