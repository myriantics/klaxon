package net.myriantics.klaxon.datagen.lang.providers.entity;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;

public final class KlaxonEnUsEntityTypeLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsEntityTypeLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        generateNonLivingEntities();
        generateLivingEntities();
    }

    private void generateNonLivingEntities() {
        addEntityType(KlaxonEntityTypes.GRAPPLE_CLAW.value(), "Steel Grapple Claw");
    }

    private void generateLivingEntities() {

    }
}
