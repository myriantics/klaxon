package net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.util.PermissionsHelper;
import net.myriantics.klaxon.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.util.BlockDirectionHelper;
import org.jetbrains.annotations.Nullable;

import static net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity.CATALYST_INDEX;
import static net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX;

public class DeepslateBlastProcessorBlock extends BlockWithEntity {

    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty FUELED = KlaxonBlockStateProperties.FUELED;
    public static final BooleanProperty HATCH_OPEN = KlaxonBlockStateProperties.HATCH_OPEN;
    public static final BooleanProperty POWERED = KlaxonBlockStateProperties.POWERED;
    public static final DirectionProperty HORIZONTAL_FACING = Properties.HORIZONTAL_FACING;

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
        if (!state.isOf(newState.getBlock()) && !world.isClient()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DeepslateBlastProcessorBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                world.updateComparators(pos, this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(DeepslateBlastProcessorBlock::new);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.getBlock() instanceof DeepslateBlastProcessorBlock && state.get(LIT) && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, state.cycle(LIT), Block.NOTIFY_LISTENERS);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DeepslateBlastProcessorBlockEntity(pos, state);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack handStack = player.getStackInHand(hand);
        Direction interactionSide = hit.getSide();

        // make sure we've got a deepslate blast processor block entity
        if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos, state, blastProcessor, interactionSide);

            // if no storage is found or we're on the client, we succeed because no further processing is needed
            if (storage == null || world.isClient()) return ItemActionResult.SUCCESS;

            // try to fastinput hand stack - if this fails, try to open GUI.
            try (Transaction tx = Transaction.openOuter()) {
                ItemStack insertedStack = player.isCreative() ? handStack.copy() : handStack;

                // if we've inserted any items, we've succeeded!
                // check if inventory is locked before performing fastinput
                if (blastProcessor.checkUnlocked(player) && canFastInput(player, state, interactionSide) && storage.insert(ItemVariant.of(insertedStack.split(1)), 1, tx) > 0) {
                    playItemInputSound(world, pos, state);
                    blastProcessor.markDirty();
                    tx.commit();
                } else {
                    tx.abort();
                    // make sure to open the screen if the insertion fails - on server
                    player.openHandledScreen(blastProcessor);
                }
            }
        }

        return ItemActionResult.SUCCESS;
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
            return (blastProcessor.getStack(CATALYST_INDEX).isEmpty() ? 0 : 7) + (blastProcessor.getStack(INGREDIENT_INDEX).isEmpty() ? 0 : 8);
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
                if (inventory.get(INGREDIENT_INDEX).isEmpty() != hatchOpen) {

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
            // deepslate blast processor has quasiconnectivity because its really funny - imagine if someone reads this because theyre digging through source to figure out why their device isnt working lol
            boolean isRecievingPower = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
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

            // update blast processor behaviors that may change on block update
            if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
                blastProcessor.updateAllActiveScreenHandlers();
            }
        }
    }

    private static void playItemInputSound(World world, BlockPos pos, BlockState state) {
        Random random = world.getRandom();

        world.playSound(null, pos, KlaxonSoundEvents.BLOCK_DEEPSLATE_BLAST_PROCESSOR_INSERT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F);

        // trip sculk sensors
        world.emitGameEvent(GameEvent.BLOCK_CLOSE, pos, GameEvent.Emitter.of(state));
    }

    public static boolean canFastInput(PlayerEntity player, BlockState state, Direction clickSide) {
        Direction blockDirection = state.get(HORIZONTAL_FACING);
        // check if you can insert from the sides
        if (!state.get(FUELED) &&
                (clickSide.equals(BlockDirectionHelper.getLeft(blockDirection)) || clickSide.equals(BlockDirectionHelper.getRight(blockDirection))
                        // prevent adventure mode players from fastinputting to catalyst slot
                        && PermissionsHelper.canModifyWorld(player))) {
            return true;
        }
        // check if you can insert from the top. if no, don't bother
        return state.get(HATCH_OPEN) && clickSide.equals(BlockDirectionHelper.getUp(blockDirection));
    }

    public static boolean isFrontObstructed(World world, BlockPos pos) {
        Direction facingDirection = world.getBlockState(pos).get(HORIZONTAL_FACING);
        return world.getBlockState(pos.offset(facingDirection, 1)).isSolidBlock(world, pos);
    }
}
