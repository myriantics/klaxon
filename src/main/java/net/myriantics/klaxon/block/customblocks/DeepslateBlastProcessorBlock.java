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
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.api.behavior.ItemBlastProcessorBehavior;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.util.BlockDirectionHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity.CATALYST_INDEX;
import static net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity.PROCESS_ITEM_INDEX;

public class DeepslateBlastProcessorBlock extends BlockWithEntity {

    public static final BooleanProperty LIT = BooleanProperty.of("lit");
    public static final BooleanProperty FUELED = BooleanProperty.of("fueled");
    public static final BooleanProperty HATCH_OPEN = BooleanProperty.of("hatch_open");
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final DirectionProperty FACING = FacingBlock.FACING;

    public static final Map<Item, BlastProcessorBehavior> BEHAVIORS = Util.make(
            new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(new ItemBlastProcessorBehavior())
    );

    public static void registerBehavior(ItemConvertible provider, BlastProcessorBehavior behavior) {
        BEHAVIORS.put(provider.asItem(), behavior);
    }

    public DeepslateBlastProcessorBlock(Settings settings) {
        super(settings);

        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
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
            int[] slots = blastProcessor.getAvailableSlots(interactionSide);
            DefaultedList<ItemStack> processorInventory = blastProcessor.getItems();


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
        }


        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if(screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
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
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACING, LIT, FUELED, HATCH_OPEN);
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
                return this.getDefaultState().with(FACING, direction);
            } else {
                return this.getDefaultState().with(FACING, direction.getOpposite());
            }
        }
        return this.getDefaultState();
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean isPowered = world.isReceivingRedstonePower(pos);
            boolean isActivated = state.get(POWERED);
            boolean isLit = state.get(LIT);
            BlockState appendedState = state;
            if (isPowered && !isActivated) {
                if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
                    blastProcessor.onRedstoneImpulse();
                    appendedState = appendedState.with(POWERED, true);
                }
            } else if(!isPowered && isActivated) {
                appendedState = appendedState.with(POWERED, false);
            }

            if (isLit != isPowered) {
                if (isLit) {
                    world.scheduleBlockTick(pos, this, 4);
                } else {
                    appendedState = state.cycle(LIT);
                }
            }
            updateBlockState(world, pos, appendedState);
        }
    }

    public static boolean canFastInput(BlockState state, Direction direction) {
        Direction blockDirection = state.get(FACING);
        // check if you can insert from the sides
        if (!state.get(FUELED) &&
                (direction.equals(BlockDirectionHelper.getLeft(direction)) || direction.equals(BlockDirectionHelper.getRight(direction)))) {
            return true;
        }
        // check if you can insert from the top. if no, don't bother
        return state.get(HATCH_OPEN) && direction.equals(BlockDirectionHelper.getUp(blockDirection));
    }
}
