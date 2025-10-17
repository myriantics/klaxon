package net.myriantics.klaxon.datagen.custom_providers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.block.Block;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;
import net.myriantics.klaxon.registry.KlaxonDynamicRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class KlaxonToolUsageRecipeTypeProvider extends FabricDynamicRegistryProvider {
    public KlaxonToolUsageRecipeTypeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    public static RegistryKey<ToolUsageRecipeType> HAMMERING = RegistryKey.of(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE, KlaxonCommon.locate("hammering"));
    public static RegistryKey<ToolUsageRecipeType> WIRECUTTING = RegistryKey.of(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE, KlaxonCommon.locate("wirecutting"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        Optional<RegistryWrapper.Impl<ToolUsageRecipeType>> wrapper = registries.getOptionalWrapper(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE);
        if (wrapper.isPresent()) {
            RegistryEntry<ToolUsageRecipeType> hammering = entries.add(
                    HAMMERING,
                    new ToolUsageRecipeType(
                            Ingredient.fromTag(KlaxonItemTags.RECIPE_PROCESSING_HAMMERS),
                            Registries.ITEM.getKey(KlaxonItems.STEEL_HAMMER)
                    )
            );

            RegistryEntry<ToolUsageRecipeType> wirecutting = entries.add(
                    WIRECUTTING,
                    new ToolUsageRecipeType(
                            Ingredient.fromTag(KlaxonItemTags.RECIPE_PROCESSING_WIRECUTTERS),
                            Registries.ITEM.getKey(KlaxonItems.STEEL_CABLE_SHEARS)
                    )
            );
        }
    }

    @Override
    public String getName() {
        return "klaxon_tool_usage_recipe_type_provider";
    }
}
