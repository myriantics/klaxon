package net.myriantics.klaxon.mixin.minecraft.tool_usage;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @WrapOperation(
            method = "useOnBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;")
    )
    public ActionResult klaxon$runToolUsageRecipe(Item instance, ItemUsageContext context, Operation<ActionResult> original) {
        ActionResult temp = ActionResult.PASS;
        if (ToolUsageRecipeLogic.test(context.getWorld(), (ItemStack) (Object) this)) {
            temp = ToolUsageRecipeLogic.runRecipeLogic(context);
        }

        // If the recipe process failed, call the original interaction
        return temp.equals(ActionResult.PASS) ? original.call(instance, context) : temp;
    }

}
