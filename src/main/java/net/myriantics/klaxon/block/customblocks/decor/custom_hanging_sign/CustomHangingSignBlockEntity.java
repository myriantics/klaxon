package net.myriantics.klaxon.block.customblocks.decor.custom_hanging_sign;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;

public class CustomHangingSignBlockEntity extends SignBlockEntity {
    private static final int MAX_TEXT_WIDTH = 60;
    private static final int TEXT_LINE_HEIGHT = 9;

    public CustomHangingSignBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(KlaxonBlockEntities.CUSTOM_HANGING_SIGN_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    public int getTextLineHeight() {
        return TEXT_LINE_HEIGHT;
    }

    @Override
    public int getMaxTextWidth() {
        return MAX_TEXT_WIDTH;
    }

    @Override
    public SoundEvent getInteractionFailSound() {
        return SoundEvents.BLOCK_HANGING_SIGN_WAXED_INTERACT_FAIL;
    }

}
