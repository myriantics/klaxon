package net.myriantics.klaxon.mixin.minecraft.damageable_and_stackable;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Properties.class)
public class ItemPropertiesMixin {

    @Inject(
            method = "buildAndValidateComponents",
            at = @At(value = "INVOKE", target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V"),
            cancellable = true)
    public void klaxon$checkForAllowlistComponent(CallbackInfoReturnable<DataComponentMap> cir, @Local DataComponentMap componentMap) {
        // feck you dont tell me what to do >:C
        if (componentMap.has(KlaxonDataComponentTypes.DAMAGEABLE_AND_STACKABLE.value())) {
            cir.setReturnValue(componentMap);
        }
    }
}
