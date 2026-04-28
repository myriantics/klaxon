package net.myriantics.klaxon.mechanics.muffling;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import org.jetbrains.annotations.Nullable;

public enum MufflerActionType implements StringRepresentable {
    APPLY(KlaxonSoundEvents.MUFFLER_APPLY_SUCCESS),
    REMOVE(KlaxonSoundEvents.MUFFLER_REMOVE_SUCCESS);

    public static final Codec<MufflerActionType> CODEC = StringRepresentable.fromEnum(MufflerActionType::values);

    public final SoundEvent success;

    MufflerActionType(SoundEvent success) {
        this.success = success;
    }

    public void playSuccessSound(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity user) {
        RandomSource random = level.getRandom();
        level.playSound(null, pos, this.success, SoundSource.BLOCKS, 0.4f + (0.4f * random.nextFloat()), 0.2f + (0.5f * random.nextFloat()));
        if (user != null) {
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(user, state));
        }
    }

    @Override
    public String getSerializedName() {
        return switch (this) {
            case APPLY -> "apply";
            case REMOVE -> "remove";
        };
    }
}
