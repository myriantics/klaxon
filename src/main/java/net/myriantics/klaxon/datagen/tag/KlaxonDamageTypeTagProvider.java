package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonDamageTypeTagProvider extends FabricTagProvider<DamageType> {

    public KlaxonDamageTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_SHIELD)
                .addOptional(KlaxonDamageTypes.HALLNOX_POD_DOMED);

        // you have to use addOptional here because shit is fucky

        getOrCreateTagBuilder(DamageTypeTags.NO_KNOCKBACK)
                .addOptional(KlaxonDamageTypes.WRENCH_OVERTUNING);

        getOrCreateTagBuilder(KlaxonDamageTypeTags.GRAPPLE_WINCH_CABLE_TRANSMISSIBLE)
                .forceAddTag(KlaxonDamageTypeTags.ELECTRICAL);

        getOrCreateTagBuilder(KlaxonDamageTypeTags.ELECTRICAL)
                .addOptional(DamageTypes.LIGHTNING_BOLT);

        getOrCreateTagBuilder(KlaxonDamageTypeTags.GRAPPLE_CLAW_DAMAGE_TYPES)
                .addOptional(KlaxonDamageTypes.GRAPPLING)
                .addOptional(KlaxonDamageTypes.RENDING);
        getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE)
                .addOptionalTag(KlaxonDamageTypeTags.GRAPPLE_CLAW_DAMAGE_TYPES);

        getOrCreateTagBuilder(KlaxonDamageTypeTags.STREAMLINE_ENCHANTMENT_CANCELS_VELOCITY_UPDATE)
                .addOptionalTag(DamageTypeTags.NO_KNOCKBACK);

        getOrCreateTagBuilder(DamageTypeTags.CAN_BREAK_ARMOR_STAND)
                .addOptional(KlaxonDamageTypes.HAMMER_BONKING)
                .addOptional(KlaxonDamageTypes.HAMMER_WALLOPING)
                .addOptional(KlaxonDamageTypes.CLEAVING)
                .addOptional(KlaxonDamageTypes.FLINT_AND_STEEELING);

        getOrCreateTagBuilder(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS)
                .addOptional(KlaxonDamageTypes.BLUDGEONING)
                .addOptional(KlaxonDamageTypes.WRENCH_OVERTUNING);

        getOrCreateTagBuilder(DamageTypeTags.DAMAGES_HELMET)
                .addOptional(KlaxonDamageTypes.HALLNOX_POD_DOMED);
    }
}
