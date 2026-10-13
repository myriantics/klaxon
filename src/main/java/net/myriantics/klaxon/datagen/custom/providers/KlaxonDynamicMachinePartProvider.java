package net.myriantics.klaxon.datagen.custom.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.util.Unit;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.NamedIngredient;
import net.myriantics.klaxon.datagen.custom.DynamicMachinePartProvider;
import net.myriantics.klaxon.mechanics.machinery.part.KlaxonDynamicMachinePartLoader;
import net.myriantics.klaxon.mechanics.machinery.part.patch.MachinePartDataComponentPatchBuilder;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class KlaxonDynamicMachinePartProvider extends DynamicMachinePartProvider {
    public KlaxonDynamicMachinePartProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildDefinitions(Output output) {
        this.add(
                output,
                NamedIngredient.fromTag(KlaxonItemTags.MUFFLERS),
                builder -> builder.addUnit(KlaxonDataComponentTypes.MACHINE_MUFFLER_PART.value())
        );
    }

    protected void add(Output output, NamedIngredient ingredient, UnaryOperator<MachinePartDataComponentPatchBuilder> operator) {
        output.accept(
                KlaxonCommon.locate(ingredient.getName()),
                new KlaxonDynamicMachinePartLoader.Definition(
                        ingredient.toIngredient(),
                        operator.apply(MachinePartDataComponentPatchBuilder.create()).build()
                )
        );
    }

    @Override
    public String getName() {
        return "klaxon_dynamic_machine_part";
    }
}
