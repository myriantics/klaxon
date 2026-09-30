package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder.IndustrialShredderTopBlockEntity;
import net.myriantics.klaxon.datagen.NamedIngredient;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class KlaxonIndustrialShreddingRecipeProvider extends KlaxonRecipeSubProvider {
    public KlaxonIndustrialShreddingRecipeProvider(KlaxonRecipeProvider provider, RecipeOutput exporter) {
        super(provider, exporter);
    }

    @Override
    public void generateRecipes() {
        addBasicWireMillingRecipe(KlaxonConventionalItemTags.IRON_PLATES, KlaxonItems.IRON_WIRE);
        addBasicWireMillingRecipe(KlaxonConventionalItemTags.GOLD_PLATES, KlaxonItems.GOLD_WIRE);
        addBasicWireMillingRecipe(KlaxonConventionalItemTags.COPPER_PLATES, KlaxonItems.COPPER_WIRE);
        addBasicWireMillingRecipe(KlaxonConventionalItemTags.STEEL_PLATES, KlaxonItems.STEEL_WIRE);
    }

    protected void addBasicWireMillingRecipe(TagKey<Item> tagKey, Holder<Item> outputHolder) {
        addIndustrialShreddingRecipe(
                NamedIngredient.fromTag(
                        tagKey
                ),
                RecipeOutputCompound.builder()
                        .guaranteed(outputHolder, 3)
                        .chance(outputHolder, 2, 0.5)
                        .build(),
                IndustrialShredderTopBlockEntity.DEFAULT_SHREDDING_TIME
        );
    }
}
