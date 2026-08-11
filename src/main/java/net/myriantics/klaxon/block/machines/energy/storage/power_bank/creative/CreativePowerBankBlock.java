package net.myriantics.klaxon.block.machines.energy.storage.power_bank.creative;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.energy.storage.power_bank.BasePowerBankBlock;
import org.jetbrains.annotations.Nullable;

public class CreativePowerBankBlock extends BasePowerBankBlock {

    public static final MapCodec<CreativePowerBankBlock> CODEC = simpleCodec(CreativePowerBankBlock::new);

    public CreativePowerBankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativePowerBankBlockEntity(pos, state);
    }
}
