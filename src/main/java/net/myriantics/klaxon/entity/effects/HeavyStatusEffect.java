package net.myriantics.klaxon.entity.effects;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class HeavyStatusEffect extends StatusEffect {
    public HeavyStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        // do nothing - you can't remove it :)
    }
}
