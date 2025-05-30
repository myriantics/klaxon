package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.component.ability.KnockbackModifierComponent;
import net.myriantics.klaxon.component.ability.ShieldBreachingComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonEntityAttributes;
import net.myriantics.klaxon.util.DualWieldHelper;
import net.myriantics.klaxon.util.LivingEntityMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityMixinAccess {

    @Unique public boolean isDualWielding = false;

    @Shadow public abstract void damageShield(float amount);

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    // Allows shield penetrating items to disable shields and deal damage through them
    @ModifyExpressionValue(
            method = "damage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z")
    )
    public boolean klaxon$shieldBreachingOverride(boolean original, @Local(argsOnly = true) DamageSource damageSource, @Local(argsOnly = true) float amount) {
        ItemStack weaponStack = damageSource.getWeaponStack();

        // make sure there's a weapon stack
        if (weaponStack != null && damageSource.getAttacker() instanceof LivingEntity attacker) {
            // make sure there is actually a shield penetration component
            ShieldBreachingComponent shieldBreachingComponent = ShieldBreachingComponent.get(weaponStack);
            if (shieldBreachingComponent != null) {
                damageShield(amount);
                takeShieldHit(attacker);

                // we have to call this independently because the hammer itself doesn't disable shields
                // it has to be walloping damage to pierce through a shield
                if (((Object)this) instanceof PlayerEntity player) {
                    player.disableShield();
                }

                // we have our own custom processing, we don't need to run the regular shield disabling stuff
                return false;
            }
        }

        // no need to retain the original since any positives are filtered out at the start
        return original;
    }

    // register KLAXON's custom entity attributes as tracked, so the game actually works :)
    @ModifyReturnValue(
            method = "createLivingAttributes",
            at = @At(value = "RETURN")
    )
    private static DefaultAttributeContainer.Builder klaxon$appendKlaxonLivingAttributes(DefaultAttributeContainer.Builder original) {
        for (RegistryEntry<EntityAttribute> attribute : KlaxonEntityAttributes.getKlaxonGenericLivingEntityAttributes()) {
            original.add(attribute);
        }
        return original;
    }

    @Inject(
            method = "swingHand(Lnet/minecraft/util/Hand;Z)V",
            at = @At(value = "HEAD")
    )
    private void klaxon$resetDualWielding(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        DualWieldHelper.syncDualWielding(self);
    }

    @Inject(
            method = "tickHandSwing",
            at = @At(value = "HEAD")
    )
    private void klaxon$untickDualWieldAnimation(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        // prevents choppy animation when players use dual wield items
        // if (self instanceof PlayerEntity player && player.getAttackCooldownProgress(0.5f) < 9) return;

        if (self instanceof PlayerEntity player && player.getAttackCooldownProgress(0.5f) < 1) return;

        klaxon$setDualWielding(false);
    }

    @Inject(
            method = "checkHandStackSwap",
            at = @At(value = "HEAD")
    )
    private void klaxon$handStackSwapCheck(Map<EquipmentSlot, ItemStack> equipmentChanges, CallbackInfo ci) {
        klaxon$setDualWielding(false);
    }

    @Inject(
            method = "clearActiveItem",
            at = @At(value = "HEAD")
    )
    private void klaxon$clearActiveItemResetDualWielding(CallbackInfo ci) {
        klaxon$setDualWielding(false);
    }

    @Inject(
            method = "stopUsingItem",
            at = @At(value = "HEAD")
    )
    private void klaxon$stopUsingItemResetDualWielding(CallbackInfo ci) {
        klaxon$setDualWielding(false);
    }

    @Override
    public boolean klaxon$isDualWielding() {
        return isDualWielding;
    }

    @Override
    public void klaxon$setDualWielding(boolean dualWielding) {
        this.isDualWielding = dualWielding;
    }
}