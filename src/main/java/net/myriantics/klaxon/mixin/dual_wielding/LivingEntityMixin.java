package net.myriantics.klaxon.mixin.dual_wielding;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.myriantics.klaxon.util.DualWieldHelper;
import net.myriantics.klaxon.util.LivingEntityMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityMixinAccess {

    @Unique public boolean isDualWielding = false;

    @Inject(
            method = "swingHand(Lnet/minecraft/util/Hand;Z)V",
            at = @At(value = "HEAD")
    )
    private void klaxon$resetDualWielding(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        DualWieldHelper.syncDualWielding(self);
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