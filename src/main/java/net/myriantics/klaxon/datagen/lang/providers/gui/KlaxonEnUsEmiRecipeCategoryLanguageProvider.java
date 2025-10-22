package net.myriantics.klaxon.datagen.lang.providers.gui;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.custom_providers.KlaxonToolUsageRecipeTypeProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

public final class KlaxonEnUsEmiRecipeCategoryLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsEmiRecipeCategoryLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        addEmiRecipeCategory(KlaxonRecipeTypes.NETHER_REACTION_RECIPE_ID, "Nether Reaction");
        addEmiRecipeCategory(KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION_ID, "Explosive Catalysts");
        addEmiRecipeCategory(KlaxonRecipeTypes.COOLING_RECIPE_ID, "Item Cooling");
        addEmiRecipeCategory(KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_ID, "Blast Processing");
        addEmiRecipeCategory(KlaxonRecipeTypes.WORLD_ITEM_APPLICATION_RECIPE_ID, "World Item Application");
        addEmiRecipeCategory(KlaxonToolUsageRecipeTypeProvider.HAMMERING.getValue().getPath(), "Hammering");
        addEmiRecipeCategory(KlaxonToolUsageRecipeTypeProvider.WIRECUTTING.getValue().getPath(), "Wirecutting");
    }
}
