package net.myriantics.klaxon.datagen.lang.providers.tag;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;

public final class KlaxonEnUsDamageTypeTagLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsDamageTypeTagLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        addTag(KlaxonDamageTypeTags.STREAMLINE_ENCHANTMENT_CANCELS_VELOCITY_UPDATE, "Streamline Enchantment Velocity Update Cancelled");
        addTag(KlaxonDamageTypeTags.ELECTRICAL, "Electrical Damage");
        addTag(KlaxonDamageTypeTags.GRAPPLE_CLAW_DAMAGE_TYPES, "Grapple Claw Damage Types");
    }
}
