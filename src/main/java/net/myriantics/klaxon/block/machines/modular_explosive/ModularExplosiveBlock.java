package net.myriantics.klaxon.block.machines.modular_explosive;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;

public class ModularExplosiveBlock extends BaseEntityBlock {

    public static final EnumProperty<FuseState> FUSE = KlaxonBlockStateProperties.FUSE;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    private static final MapCodec<ModularExplosiveBlock> CODEC = simpleCodec(ModularExplosiveBlock::new);
    public static final int DEFAULT_IGNITION_TICKS = 2;

    public ModularExplosiveBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(FUSE, FuseState.INERT)
                .setValue(TRIGGERED, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ModularExplosiveBlockEntity(pos, state);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ModularExplosiveBlockEntity blockEntity) {
            if (stack.is(KlaxonItemTags.DEFUSERS)) {
                if (state.getValue(FUSE).isCountingDown()) {
                    if (!level.isClientSide()) {
                        blockEntity.defuse();
                    }
                    return ItemInteractionResult.SUCCESS;
                }
            }

            @Nullable ExplosiveCatalystData appliedData = ExplosiveCatalystData.findRaw(level, stack);
            if (appliedData != null) {

                if (!level.isClientSide()) {
                    Component blockName = blockEntity instanceof Nameable nameable ? nameable.getDisplayName() : state.getBlock().getName();
                    final SoundEvent soundEvent;
                    final Holder.Reference<GameEvent> gameEvent;
                    ExplosiveCatalystBehavior newBehavior = appliedData.behavior(level).value();
                    if (blockEntity.getRawData().equals(appliedData) && newBehavior.relevantComponentsMatch(blockEntity.components(), stack.getComponents())) {
                        gameEvent = null;
                        soundEvent = KlaxonSoundEvents.MODULAR_EXPLOSIVE_CATALYST_MATCH;
                        player.displayClientMessage(Component.translatable("klaxon.text.actionbar.explosive_catalyst_data.matches", blockName, stack.getDisplayName()), true);
                    } else {
                        blockEntity.applyComponents(stack.getPrototype(), stack.getComponentsPatch().forget(newBehavior::isComponentIrrelevant));
                        blockEntity.setData(appliedData);
                        if (level.hasNeighborSignal(pos)) {
                            redstoneTrigger(level, pos, state);
                        }
                        if (player.isCreative()) {
                            player.displayClientMessage(Component.translatable("klaxon.text.actionbar.explosive_catalyst_data.copy_from_to", stack.getDisplayName(), blockName), true);
                            soundEvent = KlaxonSoundEvents.MODULAR_EXPLOSIVE_CLONE_CATALYST;
                        } else {
                            player.displayClientMessage(Component.translatable("klaxon.text.actionbar.explosive_catalyst_data.apply_from_to", stack.getDisplayName(), blockName), true);
                            stack.shrink(1);
                            soundEvent = KlaxonSoundEvents.MODULAR_EXPLOSIVE_INSERT_CATALYST;
                        }
                        gameEvent = GameEvent.BLOCK_CHANGE;
                    }

                    RandomSource random = level.getRandom();
                    level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, random.nextFloat() * 0.2f + 0.5f, random.nextFloat() * 0.3f + 0.3f);
                    if (gameEvent != null) {
                        level.gameEvent(player, gameEvent, pos);
                    }
                }
            }

            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.hasNeighborSignal(pos)) {
            redstoneTrigger(level, pos, state);
        }
    }

    public void updateFuseState(Level level, BlockPos pos, BlockState state, int newFuseTime, int maxFuseTime) {
        FuseState original = state.getValue(FUSE);
        FuseState newFuseState = FuseState.of(newFuseTime, maxFuseTime);
        if (original != newFuseState) {
            level.setBlockAndUpdate(pos, state.setValue(FUSE, newFuseState));
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

        if (level instanceof ServerLevel serverLevel) {
            boolean powered = level.hasNeighborSignal(pos);
            boolean triggered = state.getValue(TRIGGERED);

            if (powered != triggered) {
                if (powered) {
                    level.scheduleTick(pos, this, DEFAULT_IGNITION_TICKS);
                }

                level.setBlock(pos, state.setValue(TRIGGERED, powered), Block.UPDATE_ALL_IMMEDIATE);
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (this.shouldResetFuse(level, pos, state)) {
            this.redstoneTrigger(level, pos, state);
        }
    }

    protected boolean shouldResetFuse(Level level, BlockPos pos, BlockState state) {
        return !state.getValue(FUSE).isCountingDown();
    }

    private void redstoneTrigger(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ModularExplosiveBlockEntity blockEntity) {
            if (shouldResetFuse(level, pos, state)) {
                blockEntity.redstoneTrigger();
            }
            this.updateFuseState(level, pos, state, blockEntity.getFuseTime(), blockEntity.getMaxFuseTime());
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FUSE, TRIGGERED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState original = super.getStateForPlacement(context);

        if (context.getLevel().hasNeighborSignal(context.getClickedPos())) {
            return original == null ? null : original.setValue(TRIGGERED, true);
        } else {
            return original;
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, KlaxonBlockEntityTypes.MODULAR_EXPLOSIVE.value(), ModularExplosiveBlockEntity::serverTick);
    }
}
