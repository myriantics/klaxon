package net.myriantics.klaxon.datagen.custom.reloadable.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.NamedIngredient;
import net.myriantics.klaxon.datagen.custom.reloadable.KlaxonReloadableRegistryProvider;
import net.myriantics.klaxon.mechanics.machinery.part.DynamicMachinePartDefinition;
import net.myriantics.klaxon.mechanics.machinery.part.patch.MachinePartDataComponentPatchBuilder;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.reloadable.KlaxonDynamicMachinePartDefinitions;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class KlaxonDynamicMachinePartProvider extends KlaxonReloadableRegistryProvider<DynamicMachinePartDefinition> {
    public KlaxonDynamicMachinePartProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, KlaxonRegistries.DYNAMIC_MACHINE_PART_DEFINITION);
    }

    @Override
    protected void buildDefinitions(Output<DynamicMachinePartDefinition> output) {
        output.accept(
                KlaxonDynamicMachinePartDefinitions.MUFFLER,
                of(
                        Ingredient.of(KlaxonItemTags.MUFFLERS),
                        builder -> builder
                                .addUnit(KlaxonDataComponentTypes.MACHINE_MUFFLER_PART.value())
                )
        );
    }

    protected DynamicMachinePartDefinition of(Ingredient ingredient, UnaryOperator<MachinePartDataComponentPatchBuilder> operator) {
        return new DynamicMachinePartDefinition(
                ingredient,
                operator.apply(MachinePartDataComponentPatchBuilder.create()).build()
        );
    }
}
