package net.myriantics.klaxon.datagen.lang.providers.gui;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.registry.dynamic.KlaxonToolUsageRecipeTypes;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeTypes;

public final class KlaxonEnUsEmiRecipeCategoryLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsEmiRecipeCategoryLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        addEmiRecipeCategory(KlaxonRecipeTypes.NETHER_REACTION, "Nether Reaction");
        addEmiRecipeCategory("explosive_catalyst_definition", "Explosive Catalysts");
        addEmiRecipeCategory(KlaxonRecipeTypes.BLAST_PROCESSING, "Blast Processing");
        addEmiRecipeCategory(KlaxonRecipeTypes.WORLD_ITEM_APPLICATION, "World Item Application");
        addEmiRecipeCategory(KlaxonToolUsageRecipeTypes.HAMMERING.location().getPath(), "Hammering");
        addEmiRecipeCategory(KlaxonToolUsageRecipeTypes.WIRECUTTING.location().getPath(), "Wirecutting");
    }
}
