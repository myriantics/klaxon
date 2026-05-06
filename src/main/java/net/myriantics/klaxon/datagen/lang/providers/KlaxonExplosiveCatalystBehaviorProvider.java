package net.myriantics.klaxon.datagen.lang.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystBehaviors;

public final class KlaxonExplosiveCatalystBehaviorProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonExplosiveCatalystBehaviorProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        addExplosiveCatalystBehavior(KlaxonExplosiveCatalystBehaviors.NO_OP, "No-Op");
        addExplosiveCatalystBehavior(KlaxonExplosiveCatalystBehaviors.DEFAULT, "Vanilla Explosion");
        addExplosiveCatalystBehavior(KlaxonExplosiveCatalystBehaviors.FIREWORK_ROCKET, "Firework");
        addExplosiveCatalystBehavior(KlaxonExplosiveCatalystBehaviors.BEDLIKE, "Bedlike Explodable");
        addExplosiveCatalystBehavior(KlaxonExplosiveCatalystBehaviors.CHARGED_CREEPER_MIMIC, "Charged Creeper Mimicry");
    }
}
