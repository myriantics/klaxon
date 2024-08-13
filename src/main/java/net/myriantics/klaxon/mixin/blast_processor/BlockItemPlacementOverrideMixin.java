package net.myriantics.klaxon.mixin.blast_processor;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemPlacementOverrideMixin {

    @Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true)
    public void checkFastInput(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getWorld().getBlockEntity(context.getBlockPos()) instanceof BlastProcessorBlockEntity blastProcessor) {
            Direction dir = context.getSide();
            ItemStack handStack = context.getStack();
            int[] availableSlots = blastProcessor.getAvailableSlots(dir);

            if (availableSlots != null) {
                for (int availableSlot : availableSlots) {
                    if (blastProcessor.canInsert(availableSlot, handStack, dir)) {
                        cir.setReturnValue(ActionResult.PASS);
                        cir.cancel();
                    }
                }
            }
        }
    }
}
