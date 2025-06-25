package net.myriantics.klaxon.datagen.recipe.providers;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.registry.tag.BlockTags;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public class KlaxonExplosionConversionRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonExplosionConversionRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildNetherReactionRecipes();
    }

    private void buildNetherReactionRecipes() {
        addNetherReactionRecipe(KlaxonBlockTags.STEEL_CASING_CONVERTIBLE, KlaxonBlocks.STEEL_CASING);

        // netherrack
        addNetherReactionRecipe(KlaxonBlockTags.NETHERRACK_CONVERTIBLE, Blocks.NETHERRACK);

        // rough blackstone stuff
        addNetherReactionRecipe(KlaxonBlockTags.BLACKSTONE_CONVERTIBLE, Blocks.BLACKSTONE);
        addNetherReactionRecipe(KlaxonBlockTags.BLACKSTONE_SLAB_CONVERTIBLE, Blocks.BLACKSTONE_SLAB);
        addNetherReactionRecipe(KlaxonBlockTags.BLACKSTONE_STAIRS_CONVERTIBLE, Blocks.BLACKSTONE_STAIRS);
        addNetherReactionRecipe(KlaxonBlockTags.BLACKSTONE_WALL_CONVERTIBLE, Blocks.BLACKSTONE_WALL);

        // soul stuff
        addNetherReactionRecipe(KlaxonBlockTags.SOUL_SAND_CONVERTIBLE, Blocks.SOUL_SAND);
        addNetherReactionRecipe(KlaxonBlockTags.SOUL_SOIL_CONVERTIBLE, Blocks.SOUL_SOIL);
        addNetherReactionRecipe(KlaxonBlockTags.SOUL_TORCH_CONVERTIBLE, Blocks.SOUL_TORCH);
        addNetherReactionRecipe(KlaxonBlockTags.SOUL_WALL_TORCH_CONVERTIBLE, Blocks.SOUL_WALL_TORCH);
        addNetherReactionRecipe(KlaxonBlockTags.SOUL_LANTERN_CONVERTIBLE, Blocks.SOUL_LANTERN);
        addNetherReactionRecipe(KlaxonBlockTags.SOUL_CAMPFIRE_CONVERTIBLE, Blocks.SOUL_CAMPFIRE);

        // crimson stuff
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_NYLIUM_CONVERTIBLE, Blocks.CRIMSON_NYLIUM);
        addNetherReactionRecipe(KlaxonBlockTags.NETHER_WART_BLOCK_CONVERTIBLE, Blocks.NETHER_WART_BLOCK);
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_STEM_CONVERTIBLE, Blocks.CRIMSON_STEM);
        addNetherReactionRecipe(KlaxonBlockTags.STRIPPED_CRIMSON_STEM_CONVERTIBLE, Blocks.STRIPPED_CRIMSON_STEM);
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_HYPHAE_CONVERTIBLE, Blocks.CRIMSON_HYPHAE);
        addNetherReactionRecipe(KlaxonBlockTags.STRIPPED_CRIMSON_HYPHAE_CONVERTIBLE, Blocks.STRIPPED_CRIMSON_HYPHAE);
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_PLANKS_CONVERTIBLE, Blocks.CRIMSON_PLANKS);
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_STAIRS_CONVERTIBLE, Blocks.CRIMSON_STAIRS);
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_SLAB_CONVERTIBLE, Blocks.CRIMSON_SLAB);
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_BUTTON_CONVERTIBLE, Blocks.CRIMSON_BUTTON);
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_BUTTON_CONVERTIBLE, Blocks.CRIMSON_PRESSURE_PLATE);
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_FENCE_CONVERTIBLE, Blocks.CRIMSON_FENCE);
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_FENCE_GATE_CONVERTIBLE, Blocks.CRIMSON_FENCE_GATE);

        // warped stuff
        addNetherReactionRecipe(KlaxonBlockTags.WARPED_NYLIUM_CONVERTIBLE, Blocks.WARPED_NYLIUM);
        addNetherReactionRecipe(KlaxonBlockTags.WARPED_WART_BLOCK_CONVERTIBLE, Blocks.WARPED_WART_BLOCK);
        addNetherReactionRecipe(KlaxonBlockTags.WARPED_STEM_CONVERTIBLE, Blocks.WARPED_STEM);
    }
}
