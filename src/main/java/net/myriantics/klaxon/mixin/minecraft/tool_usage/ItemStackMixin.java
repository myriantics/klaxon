package net.myriantics.klaxon.mixin.minecraft.tool_usage;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @WrapOperation(
            method = "useOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;")
    )
    public InteractionResult klaxon$runToolUsageRecipe(Item instance, UseOnContext context, Operation<InteractionResult> original) {
        if (ToolUsageRecipeLogic.test(context.getLevel(), (ItemStack) (Object) this)) {
            switch (ToolUsageRecipeLogic.runRecipeLogic(context)) {
                case FAIL -> {
                    return original.call(instance, context);
                }
                case SUCCESS -> {
                    return InteractionResult.SUCCESS;
                }
                case COSMETIC_SUCCESS -> {
                    original.call(instance, context);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        // If the recipe process failed, call the original interaction
        return original.call(instance, context);
    }

}
