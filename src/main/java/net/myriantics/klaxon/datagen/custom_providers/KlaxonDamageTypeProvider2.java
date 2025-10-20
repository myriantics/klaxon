package net.myriantics.klaxon.datagen.custom_providers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.registry.entity.KlaxonDamageTypes;

import java.util.concurrent.CompletableFuture;

public class KlaxonDamageTypeProvider2 extends FabricDynamicRegistryProvider {
    public KlaxonDamageTypeProvider2(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        buildMeleeDamageTypes(entries);
        buildRangedDamageTypes(entries);
        buildEnvironmentalDamageTypes(entries);
    }

    private void buildMeleeDamageTypes(Entries entries) {
        addDamageType(
                entries,
                KlaxonDamageTypes.FLINT_AND_STEEELING,
                0.1f
        );
        addDamageType(
                entries,
                KlaxonDamageTypes.CLEAVING,
                0.4f
        );
        addDamageType(
                entries,
                KlaxonDamageTypes.HAMMER_BONKING,
                0.1f
        );
        addDamageType(
                entries,
                KlaxonDamageTypes.HAMMER_WALLOPING,
                0.3f
        );
        addDamageType(
                entries,
                KlaxonDamageTypes.WRENCH_OVERTUNING,
                0.4f
        );
        addDamageType(
                entries,
                KlaxonDamageTypes.BLUDGEONING,
                0.5f
        );
    }

    private void buildRangedDamageTypes(Entries entries) {
        addDamageType(
                entries,
                KlaxonDamageTypes.GRAPPLING,
                0.4f
        );
        addDamageType(
                entries,
                KlaxonDamageTypes.RENDING,
                0.8f
        );
    }

    private void buildEnvironmentalDamageTypes(Entries entries) {
        addEnvironmentalDamageType(
                entries,
                KlaxonDamageTypes.MINCING,
                0.6f
        );
        addEnvironmentalDamageType(
                entries,
                KlaxonDamageTypes.LOBOTOMY,
                1.8f
        );
    }

    private void addDamageType(Entries entries, RegistryKey<DamageType> key, float exhaustion) {
        addDamageType(entries, key, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, exhaustion);
    }

    private void addEnvironmentalDamageType(Entries entries, RegistryKey<DamageType> key, float exhaustion) {
        addDamageType(entries, key, DamageScaling.ALWAYS, exhaustion);
    }

    private void addDamageType(Entries entries, RegistryKey<DamageType> key, DamageScaling scaling, float exhaustion) {
        addDamageType(entries, key, scaling, exhaustion, DamageEffects.HURT);
    }

    private void addDamageType(Entries entries, RegistryKey<DamageType> key, DamageScaling scaling, float exhaustion, DamageEffects effects) {
        entries.add(key, new DamageType(
                key.getValue().getNamespace() + "." + key.getValue().getPath(),
                scaling,
                exhaustion,
                effects
        ));
    }

    @Override
    public String getName() {
        return "klaxon_damage_type_provider";
    }
}
