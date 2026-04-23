package net.myriantics.klaxon.mixin.minecraft.block_usage_tweak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.registry.misc.KlaxonBlockUsageTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @WrapOperation(
            method = "useItemOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/ItemInteractionResult;")
    )
    private ItemInteractionResult klaxon$executeBlockUseItemOnTweaks(Block instance, ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, Operation<ItemInteractionResult> original) {
        for (KlaxonBlockUsageTweaks.UseItemOnHandler handler : KlaxonBlockUsageTweaks.USE_ITEM_ON_HANDLERS) {
            Optional<ItemInteractionResult> result = handler.handle(instance, stack, state, level, pos, player, hand, hitResult);
            if (result.isPresent()) {
                return result.get();
            }
        }
        return original.call(instance, stack, state, level, pos, player, hand, hitResult);
    }
}
