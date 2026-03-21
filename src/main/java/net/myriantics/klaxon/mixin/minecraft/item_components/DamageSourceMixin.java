package net.myriantics.klaxon.mixin.minecraft.item_components;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.myriantics.klaxon.util.DamageSourceMixinAccess;
import org.spongepowered.asm.mixin.*;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements DamageSourceMixinAccess {
    @Mutable
    @Shadow @Final private Holder<DamageType> type;

    @Unique
    private boolean klaxon$shieldBreaching = false;

    @Unique
    @Override
    public boolean klaxon$isShieldBreaching() {
        return this.klaxon$shieldBreaching;
    }

    @Unique
    @Override
    public void klaxon$setShieldBreaching(boolean breaching) {
        this.klaxon$shieldBreaching = breaching;
    }

    @Override
    public void klaxon$setDamageType(Holder<DamageType> damageType) {
        type = damageType;
    }
}
