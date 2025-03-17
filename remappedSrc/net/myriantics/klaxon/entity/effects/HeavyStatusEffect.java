package net.myriantics.klaxon.entity.effects;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;

public class HeavyStatusEffect extends StatusEffect {
    public HeavyStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public ParticleEffect createParticle(StatusEffectInstance effect) {
        return super.createParticle(effect);
    }
}
