package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.myriantics.klaxon.registry.minecraft.KlaxonDamageTypes;

import java.util.concurrent.CompletableFuture;

public class KlaxonDamageTypeTagProvider extends FabricTagProvider<DamageType> {

    public KlaxonDamageTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_SHIELD);

        getOrCreateTagBuilder(DamageTypeTags.NO_KNOCKBACK)
                .addOptional(KlaxonDamageTypes.WRENCH_OVERTUNING);
    }
}
