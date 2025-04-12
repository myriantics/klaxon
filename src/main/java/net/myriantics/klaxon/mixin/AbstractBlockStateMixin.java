package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.api.InstabreakMiningToolItem;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
    @Shadow protected abstract BlockState asBlockState();


    // Used to ensure Hammer instabreaks blocks defined as such via tag
    @ModifyReturnValue(method = "calcBlockBreakingDelta", at = @At("RETURN"))
    private float klaxon$hammerInstabreakOverride(float original,
                                                  @Local(argsOnly = true) PlayerEntity player) {
        ItemStack miningToolStack = player.getWeaponStack();

        // check if the tool is an instabreaking tool and if the block is valid for instabreaking
        if (miningToolStack.getItem() instanceof InstabreakMiningToolItem instabreakTool
                && instabreakTool.isCorrectForInstabreak(miningToolStack, asBlockState())) {
            // if it can instabreak, set it to a value over 1.0 so that it instabreaks
            return Integer.MAX_VALUE;
        }

        // if it can't instabreak, return original
        return original;
    }
}
