package net.myriantics.klaxon.mechanics.explosive_catalyst.transformer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformer;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformerType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystTransformerTypes;
import net.myriantics.klaxon.util.DimensionTypePredicate;

public class DimensionTypeDependentExplosiveCatalystTransformer extends ExplosiveCatalystTransformer {
    private final DimensionTypePredicate predicate;
    private final boolean inverted;

    public static final MapCodec<DimensionTypeDependentExplosiveCatalystTransformer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DimensionTypePredicate.CODEC.fieldOf("dimension_type_predicate").forGetter(i -> i.predicate),
            Codec.BOOL.lenientOptionalFieldOf("inverted", false).forGetter(i -> i.inverted)
            ).apply(instance, DimensionTypeDependentExplosiveCatalystTransformer::new)
    );

    public DimensionTypeDependentExplosiveCatalystTransformer(DimensionTypePredicate predicate) {
        this(predicate, false);
    }

    public DimensionTypeDependentExplosiveCatalystTransformer(DimensionTypePredicate predicate, boolean inverted) {
        this.predicate = predicate;
        this.inverted = inverted;
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {
        // pass + is_inverted = true != true = false
        // pass + not_inverted = true != false = true
        // fail + is_inverted = false != true = true
        // fail + not_inverted = false != false = false
        // should be right
        return this.predicate.test(context.level()) != this.inverted
                ? original
                : ExplosiveCatalystData.ZERO;
    }

    @Override
    public ExplosiveCatalystTransformerType<? extends ExplosiveCatalystTransformer> getType() {
        return KlaxonExplosiveCatalystTransformerTypes.DIMENSION_TYPE_DEPENDENT;
    }
}
