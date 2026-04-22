package net.myriantics.klaxon.mechanics.muffling;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import org.jetbrains.annotations.Nullable;

public enum MufflerActionType {
    MUFFLER_APPLY(KlaxonSoundEvents.MUFFLER_APPLY_SUCCESS, KlaxonSoundEvents.MUFFLER_APPLY_FAIL),
    MUFFLER_REMOVE(KlaxonSoundEvents.MUFFLER_REMOVE_SUCCESS, KlaxonSoundEvents.MUFFLER_REMOVE_FAIL);

    public final SoundEvent success;
    public final SoundEvent fail;

    MufflerActionType(SoundEvent success, SoundEvent fail) {
        this.success = success;
        this.fail = fail;
    }

    public void playSuccess(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity user) {
        RandomSource random = level.getRandom();
        level.playSound(null, pos, this.success, SoundSource.BLOCKS, 0.4f + (0.4f * random.nextFloat()), 0.2f + (0.5f * random.nextFloat()));
        if (user != null) {
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(user, state));
        }
    }

    public void playFail(Level level, BlockPos pos, BlockState state) {
        RandomSource random = level.getRandom();
        level.playSound(null, pos, this.success, SoundSource.BLOCKS, 0.5f + (0.4f * random.nextFloat()), 0.2f + (0.2f * random.nextFloat()));
    }
}
