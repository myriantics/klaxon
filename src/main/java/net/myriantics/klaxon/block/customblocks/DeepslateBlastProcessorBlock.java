package net.myriantics.klaxon.block.customblocks;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.api.behavior.ItemBlastProcessorBehavior;
import net.myriantics.klaxon.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.util.BlockDirectionHelper;
import net.myriantics.klaxon.util.KlaxonTags;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity.CATALYST_INDEX;
import static net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity.PROCESS_ITEM_INDEX;

public class DeepslateBlastProcessorBlock extends BlockWithEntity {

    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty FUELED = KlaxonBlockStateProperties.FUELED;
    public static final BooleanProperty HATCH_OPEN = KlaxonBlockStateProperties.HATCH_OPEN;
    public static final BooleanProperty POWERED = KlaxonBlockStateProperties.POWERED;
    public static final DirectionProperty HORIZONTAL_FACING = Properties.HORIZONTAL_FACING;

    public static final Map<Item, BlastProcessorBehavior> BEHAVIORS = Util.make(
            new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(new ItemBlastProcessorBehavior())
    );

    public static void registerBehavior(ItemConvertible provider, BlastProcessorBehavior behavior) {
        BEHAVIORS.put(provider.asItem(), behavior);
    }

    public DeepslateBlastProcessorBlock(Settings settings) {
        super(settings);

        setDefaultState(getStateManager().getDefaultState()
                .with(HORIZONTAL_FACING, Direction.NORTH)
                .with(POWERED, false)
                .with(LIT, false)
                .with(FUELED, false)
                .with(HATCH_OPEN, true));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DeepslateBlastProcessorBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(DeepslateBlastProcessorBlock::new);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.isOf(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR) && state.get(LIT) && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, state.cycle(LIT), Block.NOTIFY_LISTENERS);

        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DeepslateBlastProcessorBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack handStack = player.getStackInHand(player.getActiveHand());
        Direction interactionSide = hit.getSide();

        // trying to make this viable alongside crystal and cart
        // kit would include tnt, blast processors, and redstone blocks or smthn
        if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
            int[] slots = canFastInput(state, interactionSide) ? blastProcessor.getAvailableSlots(interactionSide) : new int[] {};

            if (slots != null) {
                for (int slot : slots) {
                    if (blastProcessor.canInsert(slot, handStack, interactionSide)) {
                        ItemStack transferStack;
                        if (!player.isCreative()) {
                            transferStack = handStack.split(blastProcessor.getMaxCountPerStack());
                        } else {
                            transferStack = handStack.copy();
                        }
                        blastProcessor.setStack(slot, transferStack);
                        blastProcessor.markDirty();
                        return ActionResult.SUCCESS;
                    }
                }
            }

            if (!world.isClient) {
                player.openHandledScreen(blastProcessor);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
            return blastProcessor.getStack(CATALYST_INDEX).isEmpty() ? 0 : 15;
        }
        return 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, HORIZONTAL_FACING, LIT, FUELED, HATCH_OPEN);
    }

    public void updateBlockState(World world, BlockPos pos, @Nullable BlockState appendedState) {
        if (world.getBlockState(pos).getBlock() instanceof DeepslateBlastProcessorBlock) {
            if (appendedState == null) {
                appendedState = world.getBlockState(pos);
            }

            if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
                DefaultedList<ItemStack> inventory = blastProcessor.getItems();

                boolean hatchOpen = appendedState.get(DeepslateBlastProcessorBlock.HATCH_OPEN);
                boolean fueled = appendedState.get(DeepslateBlastProcessorBlock.FUELED);

                if (inventory.get(CATALYST_INDEX).isEmpty() == fueled) {
                    appendedState = appendedState.cycle(DeepslateBlastProcessorBlock.FUELED);
                }
                if (inventory.get(PROCESS_ITEM_INDEX).isEmpty() != hatchOpen) {
                    appendedState = appendedState.cycle(DeepslateBlastProcessorBlock.HATCH_OPEN);
                }

                if (world.getBlockState(pos) != appendedState) {
                    world.setBlockState(pos, appendedState, Block.NOTIFY_LISTENERS);
                }
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        PlayerEntity player = context.getPlayer();
        Direction direction = context.getHorizontalPlayerFacing();
        if (player != null) {
            if (player.isSneaking()) {
                return this.getDefaultState().with(HORIZONTAL_FACING, direction.getOpposite());
            } else {
                return this.getDefaultState().with(HORIZONTAL_FACING, direction);
            }
        }
        return this.getDefaultState();
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean isRecievingPower = world.isReceivingRedstonePower(pos);
            boolean frontObstructed = isFrontObstructed(world, pos);
            boolean isLit = state.get(LIT);
            boolean isPowered = state.get(POWERED);
            BlockState appendedState = state;

            if (isRecievingPower != isPowered) {
                // only pulse blast processor internals on high signal
                if (isRecievingPower) {
                    if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
                        blastProcessor.onRedstoneImpulse();
                    }
                }
                appendedState = appendedState.cycle(POWERED);
            }

            if (isLit != appendedState.get(POWERED)) {
                // don't light up block if front is obstructed
                if (!isLit && !frontObstructed) {
                    appendedState = appendedState.with(LIT, true);
                } else {
                    world.scheduleBlockTick(pos, this, 4);
                }
            }

            // if front is obstructed but it's lit, correct itself
            if ((appendedState.get(LIT) || isLit) && frontObstructed) {
                appendedState = appendedState.with(LIT, false);
            }

            updateBlockState(world, pos, appendedState);
        }
    }

    public static boolean canFastInput(BlockState state, Direction clickSide) {
        Direction blockDirection = state.get(HORIZONTAL_FACING);
        // check if you can insert from the sides
        if (!state.get(FUELED) &&
                (clickSide.equals(BlockDirectionHelper.getLeft(blockDirection)) || clickSide.equals(BlockDirectionHelper.getRight(blockDirection)))) {
            return true;
        }
        // check if you can insert from the top. if no, don't bother
        return state.get(HATCH_OPEN) && clickSide.equals(BlockDirectionHelper.getUp(blockDirection));
    }

    public static boolean isMuffled(World world, BlockPos pos) {
        BlockState blastProcessorState = world.getBlockState(pos);
        Direction direction = blastProcessorState.get(HORIZONTAL_FACING);
        return world.getBlockState(pos.offset(BlockDirectionHelper.getUp(direction))).isIn(KlaxonTags.Blocks.MACHINE_MUFFLING_BLOCKS)
                || world.getBlockState(pos.offset(BlockDirectionHelper.getFront(direction))).isIn(KlaxonTags.Blocks.MACHINE_MUFFLING_BLOCKS);
    }

    public static boolean isFrontObstructed(World world, BlockPos pos) {
        Direction facingDirection = world.getBlockState(pos).get(HORIZONTAL_FACING);
        return world.getBlockState(pos.offset(facingDirection, 1)).isSolidBlock(world, pos);
    }
}
