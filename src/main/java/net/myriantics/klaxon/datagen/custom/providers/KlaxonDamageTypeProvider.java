package net.myriantics.klaxon.datagen.custom.providers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.registry.entity.KlaxonDamageTypes;

import java.util.concurrent.CompletableFuture;

public class KlaxonDamageTypeProvider extends KlaxonDynamicRegistrySubProvider<DamageType> {

    public KlaxonDamageTypeProvider(RegistryWrapper.WrapperLookup wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {
        buildMeleeDamageTypes();
        buildRangedDamageTypes();
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
                KlaxonDamageTypes.MINCING,
                0.6f
        );
        addEnvironmentalDamageType(
                KlaxonDamageTypes.LOBOTOMY,
                1.8f
        );
    }

    private void addDamageType(RegistryKey<DamageType> key, float exhaustion) {
        addDamageType(key, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, exhaustion);
    }

    private void addEnvironmentalDamageType(RegistryKey<DamageType> key, float exhaustion) {
        addDamageType(key, DamageScaling.ALWAYS, exhaustion);
    }

    private void addDamageType(RegistryKey<DamageType> key, DamageScaling scaling, float exhaustion) {
        addDamageType(key, scaling, exhaustion, DamageEffects.HURT);
    }

    private void addDamageType(RegistryKey<DamageType> key, DamageScaling scaling, float exhaustion, DamageEffects effects) {
        this.add(key, new DamageType(
                key.getValue().getNamespace() + "." + key.getValue().getPath(),
                scaling,
                exhaustion,
                effects
        ));
    }
}
