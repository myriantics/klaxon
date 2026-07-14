package net.myriantics.klaxon.block.machines.energy.contact_charger;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class ContactChargerBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<ContactChargerBlock> CODEC = simpleCodec(ContactChargerBlock::new);
    public static final EnumProperty<AttachFace> FACE = FaceAttachedHorizontalDirectionalBlock.FACE;
    public static final DirectionProperty FACING = FaceAttachedHorizontalDirectionalBlock.FACING;

    public ContactChargerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (canInteractWithSide(hitResult.getDirection(), state) && level.getBlockEntity(pos) instanceof BaseContactChargerBlockEntity blockEntity) {
            if (!blockEntity.hasItem() && blockEntity.acceptsStack(stack)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    int selectedSlot = hand == InteractionHand.OFF_HAND ? Inventory.SLOT_OFFHAND : player.getInventory().selected;
                    blockEntity.startCharging(stack.split(1), serverPlayer, selectedSlot);
                }
                return ItemInteractionResult.SUCCESS;
            }

            if (blockEntity.hasItem() && player == blockEntity.getUser()) {
                blockEntity.refreshKeepAliveTicks();
                return ItemInteractionResult.CONSUME;
            }

            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (canInteractWithSide(hitResult.getDirection(), state) && level.getBlockEntity(pos) instanceof BaseContactChargerBlockEntity blockEntity) {
            if (blockEntity.hasItem() && player == blockEntity.getUser()) {
                blockEntity.refreshKeepAliveTicks();
                return InteractionResult.CONSUME;
            }
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);

        // fast retrieval if one so wants
        if (level.getBlockEntity(pos) instanceof BaseContactChargerBlockEntity blockEntity) {
            if (blockEntity.hasItem() && player == blockEntity.getUser()) {
                blockEntity.grantHeldStackBackToPlayer();
                blockEntity.clear();
            }
        }
    }

    protected boolean canInteractWithSide(Direction desiredInteractionSide, BlockState state) {
        return switch (state.getValue(FACE)) {
            case FLOOR -> desiredInteractionSide != Direction.DOWN;
            case WALL -> desiredInteractionSide != state.getValue(FACING).getOpposite();
            case CEILING -> desiredInteractionSide != Direction.UP;
        };
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.canAttachButGooder(level, pos, getConnectedDirection(state).getOpposite());
    }

    protected boolean canAttachButGooder(LevelReader reader, BlockPos pos, Direction direction) {
        BlockPos supportingPos = pos.relative(direction);
        return Block.canSupportCenter(reader, supportingPos, direction.getOpposite());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BaseContactChargerBlockEntity(pos, state);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof BaseContactChargerBlockEntity blockEntity) {
            return blockEntity.getAnalogSignalForChargeFullness();
        } else {
            return 0;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACE, FACING);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : (level1, blockPos, blockState, blockEntity) -> {
            if (blockEntity instanceof BaseContactChargerBlockEntity chargerBlockEntity) {
                chargerBlockEntity.serverTick(level1, blockPos, blockState);
            }
        };
    }
}
