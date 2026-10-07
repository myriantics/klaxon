package net.myriantics.klaxon.block.machines.blast_processor.steel;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlock;
import net.myriantics.klaxon.mechanics.fire_carrier.FireCarrier;
import net.myriantics.klaxon.mechanics.fire_carrier.FireCarrierInteractionContext;
import net.myriantics.klaxon.mechanics.muffling.MufflableBlock;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonFluidTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SteelBlastProcessorBlock extends AbstractBlastProcessorBlock implements MufflableBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty MUFFLED = KlaxonBlockStateProperties.MUFFLED;
    public static final EnumProperty<ExhaustStatus> EXHAUST_STATUS = KlaxonBlockStateProperties.EXHAUST_STATUS;

    public SteelBlastProcessorBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(MUFFLED, false)
                .setValue(EXHAUST_STATUS, ExhaustStatus.CLEAR)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(SteelBlastProcessorBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SteelBlastProcessorBlockEntity(KlaxonBlockEntityTypes.STEEL_BLAST_PROCESSOR.value(), pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blastProcessorBlockEntity) {
            player.openMenu(blastProcessorBlockEntity);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && !level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blastProcessor) {
                Containers.dropItemStack(
                        level,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        blastProcessor.getMuffler()
                );
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, MUFFLED, EXHAUST_STATUS);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!level.isClientSide()) {
            Direction newFacing = state.getValue(FACING);
            if (!state.is(oldState.getBlock()) || newFacing != oldState.getValue(FACING)) {
                if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blockEntity) {
                    blockEntity.exhaustFireCarrier = FireCarrier.SIDED.find(level, pos.above(), FireCarrierInteractionContext.DOWN);
                    blockEntity.frontStorageCache = ItemStorage.SIDED.find(level, pos.relative(state.getValue(FACING)), state.getValue(FACING).getOpposite());
                }
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide()) {
            if (neighborPos.equals(pos.above())) {
                BlockState neighborState = level.getBlockState(neighborPos);
                @Nullable FireCarrier carrier = FireCarrier.SIDED.find(level, neighborPos, neighborState, null, FireCarrierInteractionContext.DOWN);
                if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blockEntity) {
                    blockEntity.exhaustFireCarrier = carrier;
                }
                ExhaustStatus status = this.getExhaustStatusForAbove(level, neighborPos, neighborState, carrier);
                if (status != state.getValue(EXHAUST_STATUS)) {
                    level.setBlockAndUpdate(pos, state.setValue(EXHAUST_STATUS, status));
                }
            } else if (neighborPos.equals(pos.relative(state.getValue(FACING)))) {
                if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blockEntity) {
                    blockEntity.frontStorageCache = ItemStorage.SIDED.find(level, neighborPos, state.getValue(FACING).getOpposite());
                }
            }
        }
    }

    @Override
    protected boolean isRecievingPower(Level level, BlockPos pos) {
        return level.hasNeighborSignal(pos);
    }

    @Override
    protected int getTriggerDuration() {
        return 4;
    }

    public void handleOverload(ServerLevel level, BlockPos pos, SteelBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData catalystData) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        @Nullable FireCarrier aboveFireCarrier = blastProcessor.exhaustFireCarrier;

        if (blastProcessor.getMuffler().isEmpty()) {
            RandomSource random = level.getRandom();
            level.playSound(
                    null,
                    pos,
                    KlaxonSoundEvents.BLOCK_STEEL_BLAST_PROCESSOR_IGNITE,
                    SoundSource.BLOCKS,
                    0.3f + (0.5f * random.nextFloat()),
                    0.3f + (0.4f * random.nextFloat())
            );
        }

        if (aboveState.getBlock() instanceof SteelBlastProcessorExhaustHandler handler) {
            handler.klaxon$handleExhaust(level, abovePos, aboveState);
        } else if (aboveFireCarrier != null && aboveFireCarrier.mayIgnite()) {
            aboveFireCarrier.ignite();
        } else {
            level.setBlock(abovePos, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        }

        if (!aboveState.isCollisionShapeFullBlock(level, abovePos)) {
            List<Entity> caughtInExhaustBlast = level.getEntities(EntityTypeTest.forClass(Entity.class), new AABB(abovePos), entity -> !entity.isInvulnerable());

            float damage = (float) (catalystData.explosionPower() * 2);
            if (catalystData.producesFire()) {
                damage++;
            }

            // launched up one block for each tick of damage
            Vec3 launchVelocity = new Vec3(0, damage / 20d, 0);

            for (Entity entity : caughtInExhaustBlast) {
                if (!entity.fireImmune() && !(entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE))) {
                    entity.hurt(this.createDamageSource(level), damage);
                }
                entity.addDeltaMovement(launchVelocity);
            }
        }
    }



    public DamageSource createDamageSource(Level level) {
        return level.damageSources().source(
                KlaxonDamageTypes.FORCEFUL_EXHAUST,
                null,
                null
        );
    }

    public void updateMuffler(Level level, BlockPos pos, SteelBlastProcessorBlockEntity blastProcessor) {
        BlockState original = level.getBlockState(pos);
        BlockState newState = original.setValue(MUFFLED, !blastProcessor.getMuffler().isEmpty());

        if (!original.equals(newState)) {
            level.setBlockAndUpdate(pos, newState);
        }
    }

    protected ExhaustStatus getExhaustStatusForAbove(Level level, BlockPos abovePos, BlockState aboveState, @Nullable FireCarrier aboveFireCarrier) {
        if (this.doesStateObstructExhaust(level, abovePos, aboveState, aboveFireCarrier)) {
            // The reason that this check nested is because there are some fire carriers that can't withstand the blow of the exhaust.
            // An example of this in vanilla is candles - they can carry fire, but they're so weak structurally that they're just incinerated instead of us caring about their ignition status.
            if (aboveFireCarrier != null && aboveFireCarrier.isIgnited()) {
                return ExhaustStatus.IGNITED;
            } else {
                return ExhaustStatus.OBSTRUCTED;
            }
        } else {
            return ExhaustStatus.CLEAR;
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        @Nullable BlockState original = super.getStateForPlacement(context);

        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        ExhaustStatus status = level.isClientSide() ? ExhaustStatus.CLEAR : this.getExhaustStatusForAbove(level, pos.above(), level.getBlockState(pos), FireCarrier.SIDED.find(level, abovePos, aboveState, null, FireCarrierInteractionContext.DOWN));

        return Objects.requireNonNullElseGet(original, this::defaultBlockState).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(EXHAUST_STATUS, status);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public boolean hasMuffler(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.hasProperty(MUFFLED) && state.getValue(MUFFLED);
    }

    @Override
    public ItemStack getMuffler(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blastProcessor) {
            return blastProcessor.getMuffler();
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setMuffler(Level level, BlockPos pos, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blastProcessor) {
            blastProcessor.setMuffler(stack);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        } else {
            return (level1, blockPos, blockState, blockEntity) ->  {
                if (blockEntity instanceof SteelBlastProcessorBlockEntity steelBlastProcessor) {
                    steelBlastProcessor.serverTick();
                }
            };
        }
    }

    protected boolean doesStateObstructExhaust(Level level, BlockPos pos, BlockState state, @Nullable FireCarrier carrier) {
        if (!(state.getFluidState().isEmpty() || state.getFluidState().is(KlaxonFluidTags.STEEL_BLAST_PROCESSOR_EXHAUST_OVERWRITABLE_ALLOWLIST))) {
            return true; // modded gasolines and such should be allowed because bigger boom is funne
        }

        // overrides - denylist takes prio over allowlist
        if (state.is(KlaxonBlockTags.STEEL_BLAST_PROCESSOR_EXHAUST_OVERWRITABLE_DENYLIST)) {
            return true;
        } else if (state.is(KlaxonBlockTags.STEEL_BLAST_PROCESSOR_EXHAUST_OVERWRITABLE_ALLOWLIST)) {
            return false;
        }

        if (state.getBlock() instanceof SteelBlastProcessorExhaustHandler handler && handler.klaxon$mayHandleExhaust(level, pos, state)) {
            return false;
        }

        if (carrier != null) {
            if (carrier.mayIgnite()) {
                return false;
            }
        }

        if (state.getDestroySpeed(level, pos) == 0f || state.getCollisionShape(level, pos).isEmpty()) {
            return false;
        }

        return true;
    }
}
