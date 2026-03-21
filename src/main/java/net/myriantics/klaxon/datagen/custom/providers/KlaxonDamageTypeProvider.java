package net.myriantics.klaxon.datagen.custom.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;

public class KlaxonDamageTypeProvider extends KlaxonDynamicRegistrySubProvider<DamageType> {

    public KlaxonDamageTypeProvider(HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {
        buildMeleeDamageTypes();
        buildRangedDamageTypes();
        buildElectricalDamageTypes();
        buildEnvironmentalDamageTypes();
    }

    private void buildMeleeDamageTypes() {
        addDamageType(
                KlaxonDamageTypes.FLINT_AND_STEEELING,
                0.1f
        );
        addDamageType(
                KlaxonDamageTypes.CLEAVING,
                0.4f
        );
        addDamageType(
                KlaxonDamageTypes.HAMMER_BONKING,
                0.1f
        );
        addDamageType(
                KlaxonDamageTypes.HAMMER_WALLOPING,
                0.3f
        );
        addDamageType(
                KlaxonDamageTypes.WRENCH_OVERTUNING,
                0.4f
        );
        addDamageType(
                KlaxonDamageTypes.BLUDGEONING,
                0.5f
        );
    }

    private void buildElectricalDamageTypes() {
        addDamageType(
                KlaxonDamageTypes.GRAPPLE_CABLE_CONDUCTION,
                0.2f
        );
    }

    private void buildRangedDamageTypes() {
        addDamageType(
                KlaxonDamageTypes.GRAPPLING,
                0.4f
        );
        addDamageType(
                KlaxonDamageTypes.RENDING,
                0.8f
        );
    }

    private void buildEnvironmentalDamageTypes() {
        addEnvironmentalDamageType(
                KlaxonDamageTypes.HALLNOX_POD_DOMED,
                0.3f
        );
        addEnvironmentalDamageType(
                KlaxonDamageTypes.MINCING,
                0.6f
        );
        addEnvironmentalDamageType(
                KlaxonDamageTypes.LOBOTOMY,
                1.8f
        );
    }

    private void addDamageType(ResourceKey<DamageType> key, float exhaustion) {
        addDamageType(key, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, exhaustion);
    }

    private void addEnvironmentalDamageType(ResourceKey<DamageType> key, float exhaustion) {
        addDamageType(key, DamageScaling.ALWAYS, exhaustion);
    }

    private void addDamageType(ResourceKey<DamageType> key, DamageScaling scaling, float exhaustion) {
        addDamageType(key, scaling, exhaustion, DamageEffects.HURT);
    }

    private void addDamageType(ResourceKey<DamageType> key, DamageScaling scaling, float exhaustion, DamageEffects effects) {
        this.add(key, new DamageType(
                key.location().getNamespace() + "." + key.location().getPath(),
                scaling,
                exhaustion,
                effects
        ));
    }
}
