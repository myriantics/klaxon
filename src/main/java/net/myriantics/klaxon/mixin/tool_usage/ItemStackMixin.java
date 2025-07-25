package net.myriantics.klaxon.mixin.tool_usage;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @ModifyExpressionValue(
            method = "useOnBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;")
    )
    public ActionResult klaxon$runToolUsageRecipe(ActionResult original, @Local(argsOnly = true) ItemUsageContext context) {
        // only try to run code if no action was performed
        if (original.equals(ActionResult.PASS)) {
            ItemStack self = (ItemStack) (Object) this;
            World world = context.getWorld();

            // check that stack contains a valid tool
            if (ToolUsageRecipeLogic.test(world, self)) {
                return ToolUsageRecipeLogic.runRecipeLogic(context, original);
            }
        }
        return original;
    }

}
