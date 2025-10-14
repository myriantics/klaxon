package net.myriantics.klaxon.block.decor.custom_sign;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;

public class CustomSignBlockEntity extends SignBlockEntity {
    public CustomSignBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public boolean supports(BlockState state) {
        return this.getType().supports(state) || super.supports(state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return KlaxonBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY;
    }
}
