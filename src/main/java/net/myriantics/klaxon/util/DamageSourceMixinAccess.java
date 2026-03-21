package net.myriantics.klaxon.util;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Unique;

public interface DamageSourceMixinAccess {
    @Unique
    boolean klaxon$isShieldBreaching();

    @Unique
    void klaxon$setShieldBreaching(boolean breaching);

    @Unique
    void klaxon$setDamageType(Holder<DamageType> damageType);
}
