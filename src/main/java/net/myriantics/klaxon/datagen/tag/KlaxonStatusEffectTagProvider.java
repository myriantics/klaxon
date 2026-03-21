package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.myriantics.klaxon.tag.klaxon.KlaxonStatusEffectTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonStatusEffectTagProvider extends FabricTagProvider<MobEffect> {
    public KlaxonStatusEffectTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.MOB_EFFECT, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        // getOrCreateTagBuilder(KlaxonStatusEffectTags.HEAVY_STATUS_EFFECTS).add();
        getOrCreateTagBuilder(KlaxonStatusEffectTags.STRENGTHENING_EFFECTS)
                .add(MobEffects.DAMAGE_BOOST.value());
        getOrCreateTagBuilder(KlaxonStatusEffectTags.WEAKENING_EFFECTS)
                .add(MobEffects.WEAKNESS.value());
        getOrCreateTagBuilder(KlaxonStatusEffectTags.HASTENING_EFFECTS)
                .add(MobEffects.DIG_SPEED.value());
    }
}
