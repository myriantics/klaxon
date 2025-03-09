package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.entity.effects.KlaxonStatusEffects;
import net.myriantics.klaxon.tag.klaxon.KlaxonStatusEffectTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonStatusEffectTagProvider extends FabricTagProvider<StatusEffect> {
    public KlaxonStatusEffectTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.STATUS_EFFECT, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(KlaxonStatusEffectTags.HEAVY_STATUS_EFFECTS)
                .add(KlaxonStatusEffects.HEAVY.value());
    }
}
