package net.myriantics.klaxon.mixin.minecraft.item_components;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.util.DamageSourceMixinAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements DamageSourceMixinAccess {
    @Mutable
    @Shadow @Final private RegistryEntry<DamageType> type;

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
    public void klaxon$setDamageType(RegistryEntry<DamageType> damageType) {
        type = damageType;
    }
}
