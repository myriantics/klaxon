package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

public class KlaxonNetherReactionRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonNetherReactionRecipeProvider(KlaxonRecipeProvider provider, RecipeExporter exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        buildNetherReactionRecipes();
    }

    private void buildNetherReactionRecipes() {
        // misc
        addNetherReactionRecipe(KlaxonBlockTags.STEEL_CASING_CONVERTIBLE, KlaxonBlocks.STEEL_CASING);
        addNetherReactionRecipe(KlaxonBlockTags.AIR_CONVERTIBLE, Blocks.AIR);
        addNetherReactionRecipe(KlaxonBlockTags.FIRE_CONVERTIBLE, Blocks.FIRE);
        addNetherReactionRecipe(KlaxonBlockTags.SHROOMLIGHT_CONVERTIBLE, Blocks.SHROOMLIGHT);
        addNetherReactionRecipe(KlaxonBlockTags.BONE_BLOCK_CONVERTIBLE, Blocks.BONE_BLOCK);

        // netherrack & ores and stuff
        addNetherReactionRecipe(KlaxonBlockTags.NETHERRACK_CONVERTIBLE, Blocks.NETHERRACK);
        addNetherReactionRecipe(KlaxonBlockTags.NETHER_QUARTZ_ORE_CONVERTIBLE, Blocks.NETHER_QUARTZ_ORE);
        addNetherReactionRecipe(KlaxonBlockTags.NETHER_GOLD_ORE_CONVERTIBLE, Blocks.NETHER_GOLD_ORE);

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
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_FUNGUS_CONVERTIBLE, Blocks.CRIMSON_FUNGUS);
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_ROOTS_CONVERTIBLE, Blocks.CRIMSON_ROOTS);
        addNetherReactionRecipe(KlaxonBlockTags.NETHER_WART_BLOCK_CONVERTIBLE, Blocks.NETHER_WART_BLOCK);
        addNetherReactionRecipe(KlaxonBlockTags.WEEPING_VINE_CONVERTIBLE, Blocks.WEEPING_VINES_PLANT);
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
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_DOOR_CONVERTIBLE, Blocks.CRIMSON_DOOR);
        addNetherReactionRecipe(KlaxonBlockTags.CRIMSON_TRAPDOOR_CONVERTIBLE, Blocks.CRIMSON_TRAPDOOR);

        // warped stuff
        addNetherReactionRecipe(KlaxonBlockTags.WARPED_NYLIUM_CONVERTIBLE, Blocks.WARPED_NYLIUM);
        addNetherReactionRecipe(KlaxonBlockTags.WARPED_FUNGUS_CONVERTIBLE, Blocks.WARPED_FUNGUS);
        addNetherReactionRecipe(KlaxonBlockTags.TWISTING_VINES_CONVERTIBLE, Blocks.TWISTING_VINES_PLANT);
        addNetherReactionRecipe(KlaxonBlockTags.WARPED_WART_BLOCK_CONVERTIBLE, Blocks.WARPED_WART_BLOCK);
        addNetherReactionRecipe(KlaxonBlockTags.WARPED_STEM_CONVERTIBLE, Blocks.WARPED_STEM);
    }
}
