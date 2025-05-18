package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.custom_providers.KlaxonDamageTypeProvider;
import net.myriantics.klaxon.registry.minecraft.KlaxonDamageTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class KlaxonDamageTypeProviderImpl extends KlaxonDamageTypeProvider {
    public KlaxonDamageTypeProviderImpl(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    public Map<Identifier, DamageType> generate(Map<Identifier, DamageType> damageTypeMap) {
        accept(damageTypeMap, KlaxonDamageTypes.FLINT_AND_STEEELING, new DamageType(
                KlaxonDamageTypes.FLINT_AND_STEEELING.getValue().getPath(),
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                0.1f
        ));
        accept(damageTypeMap, KlaxonDamageTypes.CLEAVING, new DamageType(
                KlaxonDamageTypes.CLEAVING.getValue().getPath(),
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                0.4f
        ));
        accept(damageTypeMap, KlaxonDamageTypes.HAMMER_BONKING, new DamageType(
                KlaxonDamageTypes.HAMMER_BONKING.getValue().getPath(),
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                0.1f
        ));
        accept(damageTypeMap, KlaxonDamageTypes.HAMMER_WALLOPING, new DamageType(
                KlaxonDamageTypes.HAMMER_WALLOPING.getValue().getPath(),
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                0.3f
        ));
        accept(damageTypeMap, KlaxonDamageTypes.WRENCH_BASHING, new DamageType(
                KlaxonDamageTypes.WRENCH_BASHING.getValue().getPath(),
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                0.4f
        ));
        return damageTypeMap;
    }

    private void accept(Map<Identifier, DamageType> damageTypeMap, RegistryKey<DamageType> key, DamageType type) {
        damageTypeMap.put(key.getValue(), type);
    }
}
