package net.myriantics.klaxon.mixin.minecraft.tool_usage;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeLogic;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @WrapOperation(
            method = "useOnBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;")
    )
    public ActionResult klaxon$runToolUsageRecipe(Item instance, ItemUsageContext context, Operation<ActionResult> original) {
        if (ToolUsageRecipeLogic.test(context.getWorld(), (ItemStack) (Object) this)) {
            switch (ToolUsageRecipeLogic.runRecipeLogic(context)) {
                case FAIL -> {
                    return original.call(instance, context);
                }
                case SUCCESS -> {
                    return ActionResult.SUCCESS;
                }
                case COSMETIC_SUCCESS -> {
                    original.call(instance, context);
                    return ActionResult.SUCCESS;
                }
            }
        }

        // If the recipe process failed, call the original interaction
        return original.call(instance, context);
    }

}
