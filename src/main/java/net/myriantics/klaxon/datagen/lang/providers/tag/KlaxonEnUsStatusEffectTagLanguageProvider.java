package net.myriantics.klaxon.datagen.lang.providers.tag;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.tag.klaxon.KlaxonStatusEffectTags;

public final class KlaxonEnUsStatusEffectTagLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsStatusEffectTagLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        addTag(KlaxonStatusEffectTags.HEAVY_EFFECTS, "Heavy Effects");
        addTag(KlaxonStatusEffectTags.STRENGTHENING_EFFECTS, "Strengthening Effects");
        addTag(KlaxonStatusEffectTags.HASTENING_EFFECTS, "Hastening Effects");
        addTag(KlaxonStatusEffectTags.WEAKENING_EFFECTS, "Weakening Effects");
    }
}
