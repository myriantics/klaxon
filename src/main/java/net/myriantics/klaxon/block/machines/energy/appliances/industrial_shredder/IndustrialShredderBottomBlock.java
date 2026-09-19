package net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class IndustrialShredderBottomBlock extends BaseIndustrialShredderBlock {

    protected final Holder<Block> topBlock;

    @SuppressWarnings("deprecation")
    public IndustrialShredderBottomBlock(Properties properties, Holder<Block> topBlock) {
        super(properties, Part.BOTTOM);
        if (topBlock.value() instanceof IndustrialShredderTopBlock block) {
            this.topBlock = topBlock;
            block.setBottomBlock(this.builtInRegistryHolder());
        } else {
            throw new IllegalArgumentException("Provided Industrial Shredder Top Block was not an instance of " + IndustrialShredderTopBlock.class);
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IndustrialShredderBottomBlockEntity(pos, state);
    }

    @Override
    protected Holder<? extends Block> getCounterpartBlock() {
        return this.topBlock;
    }
}
