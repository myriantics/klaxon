package net.myriantics.klaxon.datagen.lang.providers.gui;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.compat.emi.KlaxonEmiRecipeCategories;
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
        addEmiRecipeCategory(KlaxonRecipeTypes.ITEM_EXPLOSION_POWER_RECIPE_ID, "Item Explosion Power");
        addEmiRecipeCategory(KlaxonRecipeTypes.COOLING_RECIPE_ID, "Item Cooling");
        addEmiRecipeCategory(KlaxonRecipeTypes.WIRECUTTING_RECIPE_ID, "Wirecutting");
        addEmiRecipeCategory(KlaxonRecipeTypes.HAMMERING_RECIPE_ID, "Hammering");
        addEmiRecipeCategory(KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_ID, "Blast Processing");
    }
}
