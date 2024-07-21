package net.myriantics.klaxon.mixin.hammer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.item.tools.HammerItem;
import net.myriantics.klaxon.util.KlaxonTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityShieldDisableMixin {

    @Shadow public abstract ItemStack getMainHandStack();

    @ModifyReturnValue(method = "disablesShield", at = @At(value = "RETURN"))
    public boolean checkHammer(boolean original) {
        return original || this.getMainHandStack().isIn(KlaxonTags.Items.SHEILD_DISABLING_MELEE);
    }
}
