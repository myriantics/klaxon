package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.cooling.ItemCoolingRecipeLogic;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @ModifyExpressionValue(
            method = "useOnBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;")
    )
    public ActionResult klaxon$runToolUsageRecipe(ActionResult original, @Local(argsOnly = true) ItemUsageContext context) {

        // only try to run code if action hasn't failed
        if (!original.equals(ActionResult.FAIL)) {
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
