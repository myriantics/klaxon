package net.myriantics.klaxon.mixin.hammer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.item.tools.HammerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class HammerShieldPunctureMixin {

    @Shadow public abstract ItemStack getMainHandStack();

    @ModifyReturnValue(method = "disablesShield", at = @At(value = "RETURN"))
    public boolean checkHammer(boolean original) {
        return original || this.getMainHandStack().getItem() instanceof HammerItem;
    }
}
