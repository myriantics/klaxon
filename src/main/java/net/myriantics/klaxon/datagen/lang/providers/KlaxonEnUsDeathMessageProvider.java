package net.myriantics.klaxon.datagen.lang.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;

public final class KlaxonEnUsDeathMessageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsDeathMessageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        generateMeleeDeathMessageTranslations();
        generateRangedDeathMessageTranslations();
        generateElectricalDeathMessageTranslations();
        generateEnvironmentalDeathMessageTranslations();
        generateMachineDeathMessageTranslations();
    }


    private void generateMeleeDeathMessageTranslations() {
        addDeathMessage(
                KlaxonDamageTypes.HAMMER_BONKING,
                "%1$s was bonked by %2$s",
                "%1$s was bonked by %2$s using %3$s"
        );
        addDeathMessage(
                KlaxonDamageTypes.HAMMER_WALLOPING,
                "%1$s was walloped by %2$s",
                "%1$s was walloped by %2$s using %3$s"
        );
        addDeathMessage(
                KlaxonDamageTypes.CLEAVING,
                "%1$s was cleaved in two by %2$s",
                "%1$s was cleaved in two by %2$s using %3$s"
        );
        addDeathMessage(
                KlaxonDamageTypes.WRENCH_OVERTUNING,
                "%1$s was overtuned by %2$s",
                "%1$s was overtuned by %2$s using %3$s"
        );
        addDeathMessage(
                KlaxonDamageTypes.FLINT_AND_STEEELING,
                "%1$s was flint and steeeled by %2$s",
                "%1$s was flint and steeeled by %2$s using %3$s"
        );
        addDeathMessage(
                KlaxonDamageTypes.BLUDGEONING,
                "%1$s was bludgeoned by %2$s",
                "%1$s was bludgeoned by %2$s using %3$s"
        );
    }

    private void generateElectricalDeathMessageTranslations() {
        addDeathMessage(
                KlaxonDamageTypes.GRAPPLE_CABLE_CONDUCTION,
                "%1$s was zapped by %2$s",
                "%1$s was zapped by %2$s using %3$s"
        );
    }

    private void generateRangedDeathMessageTranslations() {
        addDeathMessage(
                KlaxonDamageTypes.GRAPPLING,
                "%1$s was grappled by %2$s",
                "%1$s was grappled by %2$s using %3$s"
        );
        addDeathMessage(
                KlaxonDamageTypes.RENDING,
                "%1$s was rent apart by %2$s",
                "%1$s was rent apart by %2$s using %3$s"
        );
    }

    private void generateEnvironmentalDeathMessageTranslations() {
        addDeathMessage(
                KlaxonDamageTypes.HALLNOX_POD_DOMED,
                "%1$s was domed by a Hallnox Pod",
                null
        );
    }

    private void generateMachineDeathMessageTranslations() {
        addDeathMessage(
                KlaxonDamageTypes.LOBOTOMY,
                "%1$s was lobotomized by a Coring Drill",
                null
        );
        addDeathMessage(
                KlaxonDamageTypes.MINCING,
                "%1$s was minced by a Coring Drill",
                null
        );
        addDeathMessage(
                KlaxonDamageTypes.FORCEFUL_EXHAUST,
                "%1$s didn't read the manual",
                null
        );
    }
}
