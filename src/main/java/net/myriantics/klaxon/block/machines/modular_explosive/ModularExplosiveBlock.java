package net.myriantics.klaxon.block.machines.modular_explosive;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeLogic;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;

public class ModularExplosiveBlock extends BaseEntityBlock {

    public static final EnumProperty<FuseState> FUSE = KlaxonBlockStateProperties.FUSE;
    private static final MapCodec<ModularExplosiveBlock> CODEC = simpleCodec(ModularExplosiveBlock::new);

    public ModularExplosiveBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(FUSE, FuseState.INERT));
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ModularExplosiveBlockEntity blockEntity) {
            if (stack.is(KlaxonItemTags.DEFUSERS)) {
                if (!level.isClientSide() && blockEntity.isCountingDown()) {
                    blockEntity.defuse();
                }

                return ItemInteractionResult.SUCCESS;
            }

            if (player.isCreative()) {
                ExplosiveCatalystData data = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(blockEntity.createContext(), stack);
                if (!data.equals(ExplosiveCatalystData.ZERO)) {
                    if (!level.isClientSide()) {
                        blockEntity.setData(data);
                        blockEntity.applyComponentsFromItemStack(stack);
                    }
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide() && level.hasNeighborSignal(pos) && level.getBlockEntity(pos) instanceof ModularExplosiveBlockEntity blockEntity) {
            blockEntity.onRedstoneImpulse();
        }
    }



    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

        if (!level.isClientSide() && level.hasNeighborSignal(pos) && level.getBlockEntity(pos) instanceof ModularExplosiveBlockEntity blockEntity) {
            blockEntity.onRedstoneImpulse();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FUSE);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, KlaxonBlockEntities.MODULAR_EXPLOSIVE.value(), ModularExplosiveBlockEntity::serverTick);
    }
}
