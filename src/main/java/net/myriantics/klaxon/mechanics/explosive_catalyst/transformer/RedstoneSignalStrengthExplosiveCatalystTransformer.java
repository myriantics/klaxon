package net.myriantics.klaxon.mechanics.explosive_catalyst.transformer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformer;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformerType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystContextParams;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystTransformerTypes;
import org.jetbrains.annotations.Nullable;

public class RedstoneSignalStrengthExplosiveCatalystTransformer extends ExplosiveCatalystTransformer {
    final float addedMultiplier;
    final boolean requiresDirectSignal;

    public static final MapCodec<RedstoneSignalStrengthExplosiveCatalystTransformer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(-10f, 10f).lenientOptionalFieldOf("addition_multiplier", 1.0f).forGetter(i -> i.addedMultiplier),
            Codec.BOOL.lenientOptionalFieldOf("requires_direct_signal", false).forGetter(i -> i.requiresDirectSignal)
            ).apply(instance, RedstoneSignalStrengthExplosiveCatalystTransformer::new)
    );

    public RedstoneSignalStrengthExplosiveCatalystTransformer(
            float addedMultiplier,
            boolean requiresDirectSignal
    ) {
        this.addedMultiplier = addedMultiplier;
        this.requiresDirectSignal = requiresDirectSignal;
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {
        Level level = context.level();
        @Nullable BlockPos pos = context.get(KlaxonExplosiveCatalystContextParams.BLOCK_POS);
        if (pos == null) {
            return original;
        } else {
            int signal;
            if (this.requiresDirectSignal) {
                signal = level.getDirectSignalTo(pos);
            } else {
                signal = level.getBestNeighborSignal(pos);
            }
            double added = original.explosionPower() * (this.addedMultiplier * signal / 15);
            return original.copyWithPower(original.explosionPower() + added);
        }
    }

    @Override
    public ExplosiveCatalystTransformerType<? extends ExplosiveCatalystTransformer> getType() {
        return KlaxonExplosiveCatalystTransformerTypes.REDSTONE_SIGNAL_STRENGTH;
    }
}
