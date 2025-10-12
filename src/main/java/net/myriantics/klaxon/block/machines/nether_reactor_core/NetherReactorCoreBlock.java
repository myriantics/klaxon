package net.myriantics.klaxon.block.machines.nether_reactor_core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.api.Wrenchable;
import org.jetbrains.annotations.Nullable;

public class NetherReactorCoreBlock extends Block implements Wrenchable {
    public NetherReactorCoreBlock(Settings settings) {
        super(settings);
    }

    public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = Properties.HORIZONTAL_AXIS;

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 15;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!newState.isOf(this) || !state.get(HORIZONTAL_AXIS).equals(newState.get(HORIZONTAL_AXIS))) {
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState original = super.getPlacementState(ctx);
        return original == null ? null : original.with(HORIZONTAL_AXIS, ctx.getHorizontalPlayerFacing().getAxis());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(HORIZONTAL_AXIS);
    }

    @Override
    public ItemActionResult onWrenched(BlockState targetState, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        BlockPos blockPos = hitResult.getBlockPos();
        BlockState newState = targetState.cycle(HORIZONTAL_AXIS);

        world.setBlockState(blockPos, newState);
        world.playSound(
                player,
                blockPos,
                this.soundGroup.getPlaceSound(),
                SoundCategory.BLOCKS
        );
        world.emitGameEvent(
                GameEvent.BLOCK_CHANGE,
                blockPos,
                GameEvent.Emitter.of(player, newState)
        );

        return ItemActionResult.SUCCESS;
    }

    @Override
    public ItemActionResult onDispenserWrenched(BlockState targetState, BlockPos targetPos, ItemStack stack, ServerWorld serverWorld, Direction facing, BlockPointer pointer) {
        BlockState newState = targetState.cycle(HORIZONTAL_AXIS);

        serverWorld.setBlockState(targetPos, newState);
        serverWorld.playSound(
                null,
                targetPos,
                this.soundGroup.getPlaceSound(),
                SoundCategory.BLOCKS
        );
        serverWorld.emitGameEvent(
                GameEvent.BLOCK_CHANGE,
                targetPos,
                GameEvent.Emitter.of(newState)
        );

        return ItemActionResult.SUCCESS;
    }
}
