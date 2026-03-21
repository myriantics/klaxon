package net.myriantics.klaxon.mixin.minecraft.instabreaking;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.component.ability.InstabreakingToolComponent;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Shadow protected abstract BlockState asState();


    // Used to ensure Hammer instabreaks blocks defined as such via tag
    @ModifyReturnValue(method = "getDestroyProgress", at = @At("RETURN"))
    private float klaxon$hammerInstabreakOverride(float original,
                                                  @Local(argsOnly = true) Player player) {
        ItemStack miningToolStack = player.getWeaponItem();
        BlockState state = asState();

        if (
                // make sure used item is a tool
                miningToolStack.get(DataComponents.TOOL) instanceof Tool toolComponent
                // check if the tool has instabreaking component
                && InstabreakingToolComponent.get(miningToolStack) instanceof InstabreakingToolComponent instabreakingComponent
                // check if block is valid for instabreaking
                && instabreakingComponent.isCorrectForInstabreak(state)
                // make sure block is suitable for tool to mine
                && toolComponent.getMiningSpeed(state) > toolComponent.defaultMiningSpeed()
        ) {
            // pop advancement if needed
            if (player instanceof ServerPlayer serverPlayer) KlaxonAdvancementTriggers.triggerInstabreakToolInstabreak(serverPlayer, miningToolStack, state);
            // if it can instabreak, set it to a value over 1.0 so that it instabreaks
            return Integer.MAX_VALUE;
        }

        // if it can't instabreak, return original
        return original;
    }
}
