package net.myriantics.klaxon.mixin.minecraft.item_usage_tweak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.myriantics.klaxon.registry.misc.KlaxonItemUsageTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @WrapOperation(
            method = "useOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;")
    )
    private InteractionResult klaxon$pollUseOnHandlers(Item instance, UseOnContext context, Operation<InteractionResult> original) {
        for (KlaxonItemUsageTweaks.StackUseOnHandler handler : KlaxonItemUsageTweaks.USE_ON_HANDLERS) {
            KlaxonItemUsageTweaks.InteractionResultWrapper result = handler.handle(instance, context);
            if (result.isPresent()) {
                if (result.cosmeticOverride()) {
                    original.call(instance, context);
                }
                return result.get();
            }
        }
        return original.call(instance, context);
    }
}
