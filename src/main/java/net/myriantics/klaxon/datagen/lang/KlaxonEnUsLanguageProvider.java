package net.myriantics.klaxon.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.datagen.lang.providers.*;
import net.myriantics.klaxon.datagen.lang.providers.entity.KlaxonEnUsEntityAttributeProvider;
import net.myriantics.klaxon.datagen.lang.providers.entity.KlaxonEnUsEntityTypeLanguageProvider;
import net.myriantics.klaxon.datagen.lang.providers.gui.*;
import net.myriantics.klaxon.datagen.lang.providers.tag.*;

import java.util.concurrent.CompletableFuture;

public final class KlaxonEnUsLanguageProvider extends FabricLanguageProvider {
    public KlaxonEnUsLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        // tag translations
        new KlaxonEnUsItemTagLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsBlockEntityTypeTagProvider(this, translationBuilder).generate();
        new KlaxonEnUsBlockTagLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsDamageTypeTagLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsEntityTypeTagLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsFluidTagLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsStatusEffectTagLanguageProvider(this, translationBuilder).generate();

        // entity stuff
        new KlaxonEnUsEntityTypeLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsEntityAttributeProvider(this, translationBuilder).generate();

        // gui & tooltips
        new KlaxonEnUsEmiRecipeCategoryLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsEmiTextLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsItemGroupLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsJadeTextProvider(this, translationBuilder).generate();
        new KlaxonEnUsTextLanguageProvider(this, translationBuilder).generate();

        // misc
        new KlaxonEnUsBlockLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsItemLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsAdvancementLanguageProvider(this, translationBuilder).generate();
        new KlaxonEnUsDeathMessageProvider(this, translationBuilder).generate();
        new KlaxonEnUsEnchantmentLanguageProvider(this, translationBuilder).generate();
    }
}
