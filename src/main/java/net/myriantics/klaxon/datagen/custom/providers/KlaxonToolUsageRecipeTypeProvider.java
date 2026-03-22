package net.myriantics.klaxon.datagen.custom.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;
import net.myriantics.klaxon.registry.dynamic.KlaxonToolUsageRecipeTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class KlaxonToolUsageRecipeTypeProvider extends KlaxonDynamicRegistrySubProvider<ToolUsageRecipeType> {
    public KlaxonToolUsageRecipeTypeProvider(HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {
        this.add(
                KlaxonToolUsageRecipeTypes.HAMMERING,
                new ToolUsageRecipeType(
                        Ingredient.of(KlaxonItemTags.RECIPE_PROCESSING_HAMMERS),
                        KlaxonItems.STEEL_HAMMER.unwrapKey().get()
                )
        );
        this.add(
                KlaxonToolUsageRecipeTypes.WIRECUTTING,
                new ToolUsageRecipeType(
                        Ingredient.of(KlaxonItemTags.RECIPE_PROCESSING_WIRECUTTERS),
                        KlaxonItems.STEEL_CABLE_SHEARS.unwrapKey().get()
                )
        );
    }
}
