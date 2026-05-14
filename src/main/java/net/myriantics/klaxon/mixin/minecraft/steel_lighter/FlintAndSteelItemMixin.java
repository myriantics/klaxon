package net.myriantics.klaxon.mixin.minecraft.steel_lighter;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.myriantics.klaxon.item.equipment.tools.LighterItem;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlintAndSteelItem.class)
public abstract class FlintAndSteelItemMixin extends Item {
    public FlintAndSteelItemMixin(Properties properties) {
        super(properties);
    }

    @ModifyExpressionValue(
            method = "useOn",
            at = @At(value = "FIELD", target = "Lnet/minecraft/sounds/SoundEvents;FLINTANDSTEEL_USE:Lnet/minecraft/sounds/SoundEvent;", opcode = Opcodes.GETSTATIC)
    )
    private SoundEvent klaxon$overrideUseSoundForLighters(SoundEvent original) {
        if ((Object) this instanceof LighterItem lighterItem) {
            return lighterItem.getFirePlaceSound();
        } else {
            return original;
        }
    }
}
