package net.myriantics.klaxon.block.machines.energy.generators.turbine;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.BiConsumer;

public class TurbineGeneratorBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final MapCodec<TurbineGeneratorBlock> CODEC = simpleCodec(TurbineGeneratorBlock::new);

    private static final ResourceLocation WIND_CHARGE_BOOST_ID = KlaxonCommon.locate("explosion");

    public TurbineGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof TurbineGeneratorBlockEntity turbineGeneratorBlockEntity) {
            if (!level.isClientSide() && !turbineGeneratorBlockEntity.hasTurbine() && stack.is(KlaxonItemTags.TURBINE_GENERATOR_TURBINES)) {
                turbineGeneratorBlockEntity.setTurbineStack(player.isCreative() ? stack.copyWithCount(1) : stack.split(1));
            } else {
                player.openMenu(turbineGeneratorBlockEntity);
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TurbineGeneratorBlockEntity blockEntity ? blockEntity.getComparatorSignalStrength() : 0;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TurbineGeneratorBlockEntity(pos, state);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        super.onProjectileHit(level, state, hit, projectile);
        if (projectile instanceof WindCharge && level.getBlockEntity(hit.getBlockPos()) instanceof TurbineGeneratorBlockEntity blockEntity) {
            blockEntity.additionBoost(WIND_CHARGE_BOOST_ID, 10, 20);
            blockEntity.multiplicationBoost(WIND_CHARGE_BOOST_ID, 1.5, 20);
        }
    }

    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
        if (explosion.canTriggerBlocks() && level.getBlockEntity(pos) instanceof TurbineGeneratorBlockEntity blockEntity) {
            blockEntity.additionBoost(WIND_CHARGE_BOOST_ID, 5, 20);
            blockEntity.multiplicationBoost(WIND_CHARGE_BOOST_ID, 1.25, 20);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!level.isClientSide()) {
            Direction newFacing = state.getValue(FACING);
            if (!state.is(oldState.getBlock()) || newFacing != oldState.getValue(FACING)) {
                if (level.getBlockEntity(pos) instanceof TurbineGeneratorBlockEntity blockEntity) {
                    blockEntity.setTargetStorage(EnergyStorage.SIDED.find(level, pos.relative(newFacing.getOpposite()), newFacing));
                }
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

        if (!level.isClientSide()) {
            // if energy storage neighbor changed, re-poll energy storage for said neighbor
            @Nullable Direction neighborDirection = Direction.fromDelta(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getY(), neighborPos.getZ() - pos.getZ());
            if (neighborDirection != null && neighborDirection == state.getValue(FACING).getOpposite() && level.getBlockEntity(pos) instanceof TurbineGeneratorBlockEntity blockEntity) {
                blockEntity.setTargetStorage(EnergyStorage.SIDED.find(level, neighborPos, neighborDirection.getOpposite()));
            }
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide()
                ? null
                : (level1, blockPos, blockState, blockEntity) -> {
            if (blockEntity instanceof TurbineGeneratorBlockEntity turbineGeneratorBlockEntity) {
                turbineGeneratorBlockEntity.serverTick((ServerLevel) level1, blockPos, blockState);
            }
        };
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState baseState = this.defaultBlockState();
        return baseState.setValue(FACING, context.isSecondaryUseActive() ? context.getNearestLookingDirection() : context.getNearestLookingDirection().getOpposite());
    }

    public static boolean conductsTurbineThrust(Level level, BlockPos pos, BlockState state) {
        return true;
    }
}
