package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.myriantics.klaxon.block.KlaxonBlocks;

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
    }
}
