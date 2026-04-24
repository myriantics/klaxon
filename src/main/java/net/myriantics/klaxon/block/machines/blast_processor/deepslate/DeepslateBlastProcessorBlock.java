package net.myriantics.klaxon.block.machines.blast_processor.deepslate;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlock;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.util.BlockDirectionHelper;
import net.myriantics.klaxon.util.PermissionsHelper;
import org.jetbrains.annotations.Nullable;

public class DeepslateBlastProcessorBlock extends AbstractBlastProcessorBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty TRIGGERED = AbstractBlastProcessorBlock.TRIGGERED;
    public static final EnumProperty<DeepslateBlastProcessorLootState> LOOT_STATE = KlaxonBlockStateProperties.DEEPSLATE_BLAST_PROCESSOR_LOOT_STATE;
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    public DeepslateBlastProcessorBlock(Properties settings) {
        super(settings);

        registerDefaultState(getStateDefinition().any()
                .setValue(LOOT_STATE, DeepslateBlastProcessorLootState.EMPTY)
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(LIT, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(DeepslateBlastProcessorBlock::new);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (state.getBlock() instanceof DeepslateBlastProcessorBlock && state.getValue(LIT) && !world.hasNeighborSignal(pos)) {
            world.setBlock(pos, state.cycle(LIT), Block.UPDATE_CLIENTS);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DeepslateBlastProcessorBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack handStack = player.getItemInHand(hand);
        Direction interactionSide = hit.getDirection();

        // make sure we've got a deepslate blast processor block entity
        if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
            Storage<ItemVariant> storage = blastProcessor.getStorageForSide(interactionSide.getOpposite());

            // if no storage is found or we're on the client, we succeed because no further processing is needed
            if (storage == null || world.isClientSide()) return ItemInteractionResult.SUCCESS;

            // try to fastinput hand stack - if this fails, try to open GUI.
            try (Transaction tx = Transaction.openOuter()) {
                ItemStack insertedStack = player.isCreative() ? handStack.copy() : handStack;

                // if we've inserted any items, we've succeeded!
                // check if inventory is locked before performing fastinput
                if (blastProcessor.canOpen(player) && canFastInput(player, state, interactionSide) && storage.insert(ItemVariant.of(insertedStack.split(1)), 1, tx) > 0) {
                    playItemInputSound(world, pos, state);
                    blastProcessor.setChanged();
                    tx.commit();
                } else {
                    tx.abort();
                    // make sure to open the screen if the insertion fails - on server
                    player.openMenu(blastProcessor);
                }
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LOOT_STATE, HORIZONTAL_FACING, LIT);
    }

    public void updateBlockState(Level level, BlockPos pos, @Nullable BlockState appendedState) {
        if (level.getBlockState(pos).getBlock() instanceof DeepslateBlastProcessorBlock) {
            if (appendedState == null) {
                appendedState = level.getBlockState(pos);
            }

            if (level.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {

                appendedState = appendedState.setValue(LOOT_STATE, DeepslateBlastProcessorLootState.update(blastProcessor));

                if (level.getBlockState(pos) != appendedState) {
                    level.setBlock(pos, appendedState, Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        Direction direction = context.getHorizontalDirection();
        if (player != null) {
            if (player.isShiftKeyDown()) {
                return this.defaultBlockState().setValue(HORIZONTAL_FACING, direction.getOpposite());
            } else {
                return this.defaultBlockState().setValue(HORIZONTAL_FACING, direction);
            }
        }
        return this.defaultBlockState();
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClientSide) {
            // deepslate blast processor has quasiconnectivity because its really funny - imagine if someone reads this because theyre digging through source to figure out why their device isnt working lol
            boolean isRecievingPower = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
            boolean frontObstructed = isFrontObstructed(world, pos);
            boolean isLit = state.getValue(LIT);
            boolean isPowered = state.getValue(TRIGGERED);
            BlockState appendedState = state;

            if (isRecievingPower != isPowered) {
                // only pulse blast processor internals on high signal
                if (isRecievingPower) {
                    if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
                        blastProcessor.onRedstoneImpulse();
                    }
                }
                appendedState = appendedState.cycle(TRIGGERED);
            }

            if (isLit != appendedState.getValue(TRIGGERED)) {
                // don't light up block if front is obstructed
                if (!isLit && !frontObstructed) {
                    appendedState = appendedState.setValue(LIT, true);
                } else {
                    world.scheduleTick(pos, this, 4);
                }
            }

            // if front is obstructed but it's lit, correct itself
            if ((appendedState.getValue(LIT) || isLit) && frontObstructed) {
                appendedState = appendedState.setValue(LIT, false);
            }

            updateBlockState(world, pos, appendedState);
        }
    }

    private static void playItemInputSound(Level world, BlockPos pos, BlockState state) {
        RandomSource random = world.getRandom();

        world.playSound(null, pos, KlaxonSoundEvents.BLOCK_DEEPSLATE_BLAST_PROCESSOR_INSERT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F);

        // trip sculk sensors
        world.gameEvent(GameEvent.BLOCK_CLOSE, pos, GameEvent.Context.of(state));
    }

    public static boolean canFastInput(Player player, BlockState state, Direction clickSide) {
        if (!PermissionsHelper.canModifyWorld(player)) { // prevent adventure mode players from fastinputting to catalyst slot
            return false;
        }

        Direction blockDirection = state.getValue(HORIZONTAL_FACING);
        DeepslateBlastProcessorLootState lootState = state.getValue(LOOT_STATE);

        // check if you can insert from the sides
        if (!lootState.hasKnownCatalyst() && (clickSide.equals(BlockDirectionHelper.getLeft(blockDirection)) || clickSide.equals(BlockDirectionHelper.getRight(blockDirection)))) {
            return true;
        }

        // check if you can insert from the top. if no, don't bother
        return !lootState.hasKnownIngredient() && clickSide.equals(BlockDirectionHelper.getUp(blockDirection));
    }

    public static boolean isFrontObstructed(Level world, BlockPos pos) {
        Direction facingDirection = world.getBlockState(pos).getValue(HORIZONTAL_FACING);
        return world.getBlockState(pos.relative(facingDirection, 1)).isRedstoneConductor(world, pos);
    }
}
