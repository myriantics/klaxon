package net.myriantics.klaxon.mixin.anvil_adjustments.infinirepair;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {
    @WrapOperation(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;set(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1)
    )
    private <T> T klaxon$anvilUsesOverride(ItemStack instance, ComponentType<? super T> type, @Nullable T value, Operation<T> original) {
        // crappy way of overwriting anvil uses
        if (instance.isIn(KlaxonItemTags.INFINITELY_REPAIRABLE)) {
            return (T) Integer.valueOf(0);
        }
        // this throws an error in IDE but it runs fine and has caused no problems... this is fine...
        return original.call(instance, type, value);
    }
}
