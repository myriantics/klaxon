package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.util.KlaxonTags;

import java.util.function.Consumer;

public class KlaxonRecipeProvider extends FabricRecipeProvider {
    public KlaxonRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)
                .pattern("ppp")
                .pattern("pdp")
                .pattern("ppp")
                .input('p', Blocks.POLISHED_DEEPSLATE)
                .input('d', Blocks.DISPENSER)
                .criterion(FabricRecipeProvider.hasItem(Blocks.POLISHED_DEEPSLATE),
                        FabricRecipeProvider.conditionsFromItem(Blocks.POLISHED_DEEPSLATE))
                .criterion(FabricRecipeProvider.hasItem(Blocks.DISPENSER),
                        FabricRecipeProvider.conditionsFromItem(Blocks.DISPENSER))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, KlaxonBlocks.STEEL_BLOCK)
                .pattern("sss")
                .pattern("sss")
                .pattern("sss")
                .input('s', KlaxonItems.STEEL_INGOT)
                .criterion(FabricRecipeProvider.hasItem(KlaxonItems.STEEL_INGOT),
                        FabricRecipeProvider.conditionsFromItem(KlaxonItems.STEEL_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, KlaxonItems.HAMMER)
                .pattern("bib")
                .pattern(" s ")
                .pattern("lsl")
                .input('b', KlaxonTags.Items.STEEL_BLOCKS)
                .input('i', KlaxonTags.Items.STEEL_INGOTS)
                .input('s', Items.STICK)
                .input('l', Items.LEATHER)
                .criterion(FabricRecipeProvider.hasItem(KlaxonItems.STEEL_INGOT),
                        FabricRecipeProvider.conditionsFromItem(KlaxonItems.STEEL_INGOT))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, KlaxonItems.STEEL_INGOT, 9)
                .input(KlaxonTags.Items.STEEL_BLOCKS)
                .criterion(FabricRecipeProvider.hasItem(KlaxonItems.STEEL_INGOT),
                        FabricRecipeProvider.conditionsFromItem(KlaxonItems.STEEL_INGOT))
                .offerTo(exporter);


    }
}
