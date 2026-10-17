package net.myriantics.klaxon.datagen.custom.reloadable.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.klaxon.datagen.custom.reloadable.KlaxonReloadableRegistryProvider;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.reloadable.KlaxonToolUsageRecipeTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonToolUsageRecipeTypeProvider extends KlaxonReloadableRegistryProvider<ToolUsageRecipeType> {
    public KlaxonToolUsageRecipeTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, KlaxonRegistries.TOOL_USAGE_RECIPE_TYPE);
    }

    @Override
    protected void buildDefinitions(Output<ToolUsageRecipeType> output) {
        output.accept(
                KlaxonToolUsageRecipeTypes.HAMMERING,
                new ToolUsageRecipeType(
                        Ingredient.of(KlaxonItemTags.RECIPE_PROCESSING_HAMMERS),
                        KlaxonItems.STEEL_HAMMER.unwrapKey().get()
                )
        );
        output.accept(
                KlaxonToolUsageRecipeTypes.WIRECUTTING,
                new ToolUsageRecipeType(
                        Ingredient.of(KlaxonItemTags.RECIPE_PROCESSING_WIRECUTTERS),
                        KlaxonItems.STEEL_CABLE_SHEARS.unwrapKey().get()
                )
        );
    }
}
