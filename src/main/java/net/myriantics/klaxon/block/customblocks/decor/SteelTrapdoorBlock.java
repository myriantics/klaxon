package net.myriantics.klaxon.block.customblocks.decor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.Wrenchable;

public class SteelTrapdoorBlock extends TrapdoorBlock implements Wrenchable {
    public SteelTrapdoorBlock(BlockSetType type, Settings settings) {
        super(type, settings);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient()) {
            boolean isRecievingPower = world.isReceivingRedstonePower(pos);

            if (isRecievingPower != state.get(POWERED)) {
                BlockState newState = state.cycle(POWERED);

                // flips on up signal
                if (isRecievingPower) {
                    this.playToggleSound(null, world, pos, !state.get(OPEN));
                    newState = newState.cycle(OPEN);

                    if (state.get(WATERLOGGED)) {
                        world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
                    }
                }

                world.setBlockState(pos, newState);
            }
        }
    }

    @Override
    public ItemActionResult onWrenched(BlockState targetState, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        BlockPos targetPos = hitResult.getBlockPos();

        this.playToggleSound(player, world, targetPos, !targetState.get(OPEN));
        world.setBlockState(targetPos, targetState.cycle(OPEN));

        return ItemActionResult.SUCCESS;
    }

    @Override
    public ItemActionResult onDispenserWrenched(BlockState targetState, BlockPos targetPos, ItemStack stack, ServerWorld serverWorld, Direction facing, BlockPointer pointer) {

        this.playToggleSound(null, serverWorld, targetPos, !targetState.get(OPEN));
        serverWorld.setBlockState(targetPos, targetState.cycle(OPEN));

        return ItemActionResult.SUCCESS;
    }
}
