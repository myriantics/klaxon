package net.myriantics.klaxon.block.decor.custom_hanging_sign;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;

public class CustomHangingSignBlockEntity extends HangingSignBlockEntity {
    public CustomHangingSignBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    @Override
    public boolean supports(BlockState state) {
        return this.getType().supports(state) || super.supports(state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return KlaxonBlockEntities.CUSTOM_HANGING_SIGN_BLOCK_ENTITY;
    }
}
