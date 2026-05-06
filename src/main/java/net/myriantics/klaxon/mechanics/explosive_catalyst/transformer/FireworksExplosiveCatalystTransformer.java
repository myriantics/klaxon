package net.myriantics.klaxon.mechanics.explosive_catalyst.transformer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformer;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformerType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystTransformerTypes;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FireworksExplosiveCatalystTransformer extends ExplosiveCatalystTransformer {

    private final FireworkExplosionExplosiveCatalystTransformer starTransformer;
    private final ExplosiveCatalystData flightDurationData;

    public static final MapCodec<FireworksExplosiveCatalystTransformer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FireworkExplosionExplosiveCatalystTransformer.CODEC.fieldOf("star_transformer").forGetter(i -> i.starTransformer),
            ExplosiveCatalystData.CODEC.fieldOf("flight_duration_data").forGetter(i -> i.flightDurationData)
            ).apply(instance, FireworksExplosiveCatalystTransformer::new)
    );

    public FireworksExplosiveCatalystTransformer(FireworkExplosionExplosiveCatalystTransformer starTransformer, ExplosiveCatalystData flightDurationData) {
        this.starTransformer = starTransformer;
        this.flightDurationData = flightDurationData;
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {
        @Nullable Fireworks fireworks = context.components().get(DataComponents.FIREWORKS);

        if (fireworks == null) {
            return original;
        } else {
            double explosionPower = original.explosionPower();
            boolean fiery = original.producesFire();

            ExplosiveCatalystData flightDurationData = this.flightDurationData.get(context.level()).value().transformExplosiveCatalystData(context, this.flightDurationData);
            explosionPower += fireworks.flightDuration() * flightDurationData.explosionPower();
            fiery |= flightDurationData.producesFire();

            ExplosiveCatalystData result = new ExplosiveCatalystData(original.behavior(), explosionPower, fiery);
            for (FireworkExplosion explosion : fireworks.explosions()) {
                result = this.starTransformer.transform(context, explosion, result);
            }

            // each recipe produces 3 rockets, so our actual value is 1/3 of what was calculated - then round to the nearest tenth.
            return result.copyWithPower(result.explosionPower() / 3);
        }
    }

    @Override
    public ExplosiveCatalystTransformerType<? extends ExplosiveCatalystTransformer> getType() {
        return KlaxonExplosiveCatalystTransformerTypes.FIREWORKS;
    }
}
