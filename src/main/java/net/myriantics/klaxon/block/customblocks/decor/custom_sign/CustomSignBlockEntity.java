package net.myriantics.klaxon.block.customblocks.decor.custom_sign;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;

public class CustomSignBlockEntity extends SignBlockEntity {
    public CustomSignBlockEntity(BlockPos pos, BlockState state) {
        super(KlaxonBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY, pos, state);
    }
}
