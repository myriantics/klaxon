package net.myriantics.klaxon.util;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

public interface DamageSourceMixinAccess {
    @Unique
    @Nullable ShieldBreachingComponent klaxon$getShieldBreachingComponent();

    @Unique
    void klaxon$setShieldBreachingComponent(ShieldBreachingComponent shieldBreachingComponent);

    @Unique
    void klaxon$setDamageType(RegistryEntry<DamageType> damageType);
}
