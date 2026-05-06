package net.myriantics.klaxon.mechanics.explosive_catalyst.transformer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformer;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformerType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystTransformerTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FireworkExplosionExplosiveCatalystTransformer extends ExplosiveCatalystTransformer {

    private final Map<FireworkExplosion.Shape, ExplosiveCatalystData> shape2CatalystData;
    private final ExplosiveCatalystData trailData;
    private final ExplosiveCatalystData twinkleData;

    private static final Codec<Map<FireworkExplosion.Shape, ExplosiveCatalystData>> SHAPE_2_CATALYST_DATA_CODEC = Codec.simpleMap(
            FireworkExplosion.Shape.CODEC,
            ExplosiveCatalystData.CODEC,
            StringRepresentable.keys(FireworkExplosion.Shape.values())
    ).codec();

    public static final MapCodec<FireworkExplosionExplosiveCatalystTransformer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SHAPE_2_CATALYST_DATA_CODEC.fieldOf("shapes_to_data").forGetter(i -> i.shape2CatalystData),
            ExplosiveCatalystData.CODEC.fieldOf("trail_data").forGetter(i -> i.trailData),
            ExplosiveCatalystData.CODEC.fieldOf("twinkle_data").forGetter(i -> i.twinkleData)
            ).apply(instance, FireworkExplosionExplosiveCatalystTransformer::new)
    );

    public FireworkExplosionExplosiveCatalystTransformer(Map<FireworkExplosion.Shape, ExplosiveCatalystData> shape2CatalystData, ExplosiveCatalystData trailData, ExplosiveCatalystData twinkleData) {
        this.shape2CatalystData = shape2CatalystData;
        this.trailData = trailData;
        this.twinkleData = twinkleData;
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {
        @Nullable FireworkExplosion explosion = context.components().get(DataComponents.FIREWORK_EXPLOSION);
        if (explosion == null) {
            return original;
        } else {
            return this.transform(context, explosion, original);
        }
    }

    public ExplosiveCatalystData transform(ExplosiveCatalystContext context, FireworkExplosion explosion, ExplosiveCatalystData data) {
        double explosionPower = data.explosionPower();
        boolean fiery = data.producesFire();

        @Nullable ExplosiveCatalystData shapeData = this.shape2CatalystData.get(explosion.shape());
        if (shapeData != null) {
            shapeData = shapeData.get(context.level()).value().transformExplosiveCatalystData(context, shapeData);
            explosionPower += shapeData.explosionPower();
            fiery |= shapeData.producesFire();
        }

        if (explosion.hasTrail()) {
            ExplosiveCatalystData trailData = this.trailData.get(context.level()).value().transformExplosiveCatalystData(context, this.trailData);
            explosionPower += trailData.explosionPower();
            fiery |= trailData.producesFire();
        }

        if (explosion.hasTwinkle()) {
            ExplosiveCatalystData twinkleData = this.twinkleData.get(context.level()).value().transformExplosiveCatalystData(context, this.twinkleData);
            explosionPower += twinkleData.explosionPower();
            fiery |= twinkleData.producesFire();
        }

        return new ExplosiveCatalystData(data.behavior(), explosionPower, fiery);
    }

    @Override
    public ExplosiveCatalystTransformerType<? extends ExplosiveCatalystTransformer> getType() {
        return KlaxonExplosiveCatalystTransformerTypes.FIREWORK_EXPLOSION;
    }
}
