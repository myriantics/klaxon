package net.myriantics.klaxon.mixin;

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
    private ShieldBreachingComponent klaxaon$shieldBreachingComponent = null;

    @Unique
    @Override
    public @Nullable ShieldBreachingComponent klaxon$getShieldBreachingComponent() {
        return klaxaon$shieldBreachingComponent;
    }

    @Unique
    @Override
    public void klaxon$setShieldBreachingComponent(ShieldBreachingComponent shieldBreachingComponent) {
        this.klaxaon$shieldBreachingComponent = shieldBreachingComponent;
    }

    @Override
    public void klaxon$setDamageType(RegistryEntry<DamageType> damageType) {
        type = damageType;
    }
}
