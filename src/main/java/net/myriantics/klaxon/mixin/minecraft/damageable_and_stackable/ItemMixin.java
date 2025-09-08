package net.myriantics.klaxon.mixin.minecraft.damageable_and_stackable;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Settings.class)
public class ItemMixin {

    @Inject(
            method = "getValidatedComponents",
            at = @At(value = "INVOKE", target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V"),
            cancellable = true)
    public void klaxon$checkForAllowlistComponent(CallbackInfoReturnable<ComponentMap> cir, @Local ComponentMap componentMap) {
        // feck you dont tell me what to do >:C
        if (componentMap.contains(KlaxonDataComponentTypes.DAMAGEABLE_AND_STACKABLE)) {
            cir.setReturnValue(componentMap);
        }
    }
}
