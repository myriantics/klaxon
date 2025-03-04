package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.block.AbstractBlock$AbstractBlockState")
public abstract class AbstractBlockStateMixin {
    @Shadow protected abstract BlockState asBlockState();


    // Used to ensure Hammer instabreaks blocks defined as such via tag
    @ModifyReturnValue(method = "calcBlockBreakingDelta", at = @At("RETURN"))
    private float klaxon$hammerInstabreakOverride(float original,
                                                  @Local(argsOnly = true) PlayerEntity player) {
        // yonk the block state
        if (asBlockState().isIn(KlaxonBlockTags.HAMMER_INSTABREAKABLE)
                && player.getMainHandStack().getItem() instanceof HammerItem) {
            // if it can instabreak, set it to a value over 1.0 so that it instabreaks
            return Integer.MAX_VALUE;
        }

        // if it can't instabreak, return original
        return original;
    }
}
