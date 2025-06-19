package net.myriantics.klaxon.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.util.DamageSourceMixinAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public class DamageSourceMixin implements DamageSourceMixinAccess {
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
}
