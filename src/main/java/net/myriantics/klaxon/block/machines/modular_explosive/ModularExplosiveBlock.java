package net.myriantics.klaxon.block.machines.modular_explosive;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipeLogic;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
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
            if (stack.is(KlaxonItemTags.DEFUSERS) && state.getValue(FUSE).isCountingDown()) {
                if (!level.isClientSide()) {
                    blockEntity.defuse();
                }

                return ItemInteractionResult.SUCCESS;
            }

            if (player.isCreative()) {
                if (level instanceof ServerLevel serverLevel) {
                    ExplosiveCatalystData data = ExplosiveCatalystDefinitionRecipeLogic.computeRawExplosiveCatalystData(serverLevel, stack);
                    if (data != null) {
                        if (!level.isClientSide()) {
                            blockEntity.setData(data);
                            blockEntity.applyComponentsFromItemStack(stack);
                            if (level.hasNeighborSignal(pos)) {
                                onRedstoneImpulse(level, pos, state);
                            }
                            Component blockName = blockEntity instanceof Nameable nameable ? nameable.getDisplayName() : state.getBlock().getName();
                            player.displayClientMessage(Component.translatable("klaxon.text.actionbar.catalyst_copy_from_to", stack.getDisplayName(), blockName), true);
                        }

                    }
                }

                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.hasNeighborSignal(pos)) {
            onRedstoneImpulse(level, pos, state);
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
                int ignitionTicks = level.getBlockEntity(pos) instanceof ModularExplosiveBlockEntity be ? be.getIgnitionTicks() : DEFAULT_IGNITION_TICKS;

                if (powered && ignitionTicks != -1) { // -1 ignition ticks disables ignition
                    if (ignitionTicks == 0) { // 0 ignition ticks pops immediately
                        this.tick(state, serverLevel, pos, level.getRandom());
                    } else {
                        level.scheduleTick(pos, this, level.getBlockEntity(pos) instanceof ModularExplosiveBlockEntity be ? be.getIgnitionTicks() : DEFAULT_IGNITION_TICKS);
                    }
                }

                level.setBlock(pos, state.setValue(TRIGGERED, powered), Block.UPDATE_ALL_IMMEDIATE);
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (this.shouldResetFuse(level, pos, state)) {
            this.onRedstoneImpulse(level, pos, state);
        }
    }

    protected boolean shouldResetFuse(Level level, BlockPos pos, BlockState state) {
        return !state.getValue(FUSE).isCountingDown();
    }

    private void onRedstoneImpulse(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ModularExplosiveBlockEntity blockEntity) {
            if (shouldResetFuse(level, pos, state)) {
                blockEntity.onRedstoneImpulse();
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
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, KlaxonBlockEntityTypes.MODULAR_EXPLOSIVE.value(), ModularExplosiveBlockEntity::serverTick);
    }
}
