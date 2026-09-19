package net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class IndustrialShredderTopBlock extends BaseIndustrialShredderBlock {

    public static final DirectionProperty FACING = BaseIndustrialShredderBlock.FACING;

    protected Holder<Block> bottomBlock = null;

    public IndustrialShredderTopBlock(Properties properties) {
        super(properties, Part.TOP);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    void setBottomBlock(Holder<Block> holder) {
        if (holder.value() instanceof IndustrialShredderBottomBlock) {
            this.bottomBlock = holder;
        } else {
            throw new IllegalArgumentException("Attempted to set Industrial Shredder Top Block's stored reference of its Bottom Block to an invalid value.");
        }
    }

    @Override
    protected Holder<? extends Block> getCounterpartBlock() {
        return this.bottomBlock;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IndustrialShredderTopBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }

        return (level1, blockPos, blockState, blockEntity) -> {
            if (blockEntity instanceof IndustrialShredderTopBlockEntity shredderTop) {
                shredderTop.serverTick(level, blockPos, blockState);
            }
        };
    }
}
