package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.registry.minecraft.KlaxonAdvancementTriggers;
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
        BlockState state = asBlockState();

        if (
                // make sure used item is a tool
                miningToolStack.get(DataComponentTypes.TOOL) instanceof ToolComponent toolComponent
                // check if the tool has instabreaking component
                && InstabreakingToolComponent.get(miningToolStack) instanceof InstabreakingToolComponent instabreakingComponent
                // check if block is valid for instabreaking
                && instabreakingComponent.isCorrectForInstabreak(state)
                // make sure block is suitable for tool to mine
                && toolComponent.getSpeed(state) > toolComponent.defaultMiningSpeed()
        ) {
            // pop advancement if needed
            if (player instanceof ServerPlayerEntity serverPlayer) KlaxonAdvancementTriggers.triggerInstabreakToolInstabreak(serverPlayer, miningToolStack, state);
            // if it can instabreak, set it to a value over 1.0 so that it instabreaks
            return Integer.MAX_VALUE;
        }

        // if it can't instabreak, return original
        return original;
    }
}
