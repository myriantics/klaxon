package net.myriantics.klaxon.datagen.recipe.providers;

import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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

        addIndustrialShreddingRecipe(
                NamedIngredient.ofItems(Items.HONEYCOMB_BLOCK),
                RecipeOutputCompound.builder()
                        .guaranteed(Items.HONEYCOMB, 3)
                        .chance(Items.HONEYCOMB, 0.5)
                        .build(),
                IndustrialShredderTopBlockEntity.DEFAULT_SHREDDING_TIME
        );
        addIndustrialShreddingRecipe(
                NamedIngredient.ofItems(Items.DRIED_KELP_BLOCK),
                RecipeOutputCompound.builder()
                        .guaranteed(Items.DRIED_KELP, 3)
                        .chance(Items.DRIED_KELP, 3, 0.4)
                        .build(),
                IndustrialShredderTopBlockEntity.DEFAULT_SHREDDING_TIME
        );
        addIndustrialShreddingRecipe(
                NamedIngredient.ofItems(
                        Items.COPPER_GRATE,
                        Items.EXPOSED_COPPER_GRATE,
                        Items.WEATHERED_COPPER_GRATE,
                        Items.OXIDIZED_COPPER_GRATE,
                        Items.WAXED_COPPER_GRATE,
                        Items.WAXED_EXPOSED_COPPER_GRATE,
                        Items.WAXED_WEATHERED_COPPER_GRATE,
                        Items.WAXED_OXIDIZED_COPPER_GRATE
                ),
                RecipeOutputCompound.builder()
                        .guaranteed(KlaxonItems.COPPER_WIRE, 4)
                        .chance(KlaxonItems.COPPER_WIRE, 2, 0.4)
                        .build(),
                IndustrialShredderTopBlockEntity.DEFAULT_SHREDDING_TIME
        );
        addIndustrialShreddingRecipe(
                NamedIngredient.fromTag(ItemTags.WOOL),
                RecipeOutputCompound.builder()
                        .guaranteed(Items.STRING)
                        .chance(Items.STRING, 0.3)
                        .build(),
                IndustrialShredderTopBlockEntity.DEFAULT_SHREDDING_TIME
        );
        addIndustrialShreddingRecipe(
                NamedIngredient.fromTag(ItemTags.WOOL_CARPETS),
                RecipeOutputCompound.builder()
                        .chance(Items.STRING, 0.2)
                        .build(),
                IndustrialShredderTopBlockEntity.DEFAULT_SHREDDING_TIME / 2
        );
        addIndustrialShreddingRecipe(
                NamedIngredient.fromTag(ItemTags.BANNERS),
                RecipeOutputCompound.builder()
                        .guaranteed(Items.STRING, 3)
                        .chance(Items.STICK, 1, 0.5)
                        .chance(Items.STRING, 2, 0.4)
                        .build(),
                IndustrialShredderTopBlockEntity.DEFAULT_SHREDDING_TIME
        );
        addIndustrialShreddingRecipe(
                NamedIngredient.ofItems(
                        Items.SCULK
                ),
                RecipeOutputCompound.builder()
                        .guaranteed(Items.SCULK_VEIN, 2)
                        .chance(Items.SCULK_VEIN, 2, 0.3)
                        .build(),
                IndustrialShredderTopBlockEntity.DEFAULT_SHREDDING_TIME
        );
        addIndustrialShreddingRecipe(
                NamedIngredient.ofItems(Items.ELYTRA),
                RecipeOutputCompound.builder()
                        .guaranteed(Items.PHANTOM_MEMBRANE, 4)
                        .chance(Items.PHANTOM_MEMBRANE, 2, 0.7)
                        .build(),
                IndustrialShredderTopBlockEntity.DEFAULT_SHREDDING_TIME
        );
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
