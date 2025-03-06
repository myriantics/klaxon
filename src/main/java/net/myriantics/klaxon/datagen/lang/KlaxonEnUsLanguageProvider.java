package net.myriantics.klaxon.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class KlaxonEnUsLanguageProvider extends FabricLanguageProvider {
    protected KlaxonEnUsLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {

    }

    private static void generateItemTranslations(TranslationBuilder translationBuilder) {

    }

    private static void generateBlockTranslations() {

    }
}
