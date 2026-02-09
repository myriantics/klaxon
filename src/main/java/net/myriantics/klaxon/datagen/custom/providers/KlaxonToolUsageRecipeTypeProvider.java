package net.myriantics.klaxon.datagen.custom.providers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.dynamic.KlaxonToolUsageRecipeTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class KlaxonToolUsageRecipeTypeProvider extends KlaxonDynamicRegistrySubProvider<ToolUsageRecipeType> {
    public KlaxonToolUsageRecipeTypeProvider(RegistryWrapper.WrapperLookup wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {
        this.add(
                KlaxonToolUsageRecipeTypes.HAMMERING,
                new ToolUsageRecipeType(
                        Ingredient.fromTag(KlaxonItemTags.RECIPE_PROCESSING_HAMMERS),
                        Registries.ITEM.getKey(KlaxonItems.STEEL_HAMMER)
                )
        );
        this.add(
                KlaxonToolUsageRecipeTypes.WIRECUTTING,
                new ToolUsageRecipeType(
                        Ingredient.fromTag(KlaxonItemTags.RECIPE_PROCESSING_WIRECUTTERS),
                        Registries.ITEM.getKey(KlaxonItems.STEEL_CABLE_SHEARS)
                )
        );
    }
}
