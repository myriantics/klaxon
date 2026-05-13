package net.myriantics.klaxon.mechanics.explosive_catalyst;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystHandlers;

import java.util.Arrays;
import java.util.List;

public class ExplosiveCatalystBehavior {

    public final Holder<ExplosiveCatalystHandler> handlerHolder;
    public final List<ExplosiveCatalystTransformer> transformers;
    public final int color;

    public static final Codec<ExplosiveCatalystBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            KlaxonBuiltInRegistries.EXPLOSIVE_CATALYST_HANDLERS.holderByNameCodec().fieldOf("handler").forGetter(i -> i.handlerHolder),
            ExplosiveCatalystTransformer.LIST_CODEC.fieldOf("transformers").forGetter(i -> i.transformers),
            Codec.INT.fieldOf("color").forGetter(i -> i.color)
    ).apply(instance, ExplosiveCatalystBehavior::new));

    public ExplosiveCatalystBehavior(Holder<ExplosiveCatalystHandler> handlerHolder, int color, ExplosiveCatalystTransformer... transformers) {
        this(handlerHolder, Arrays.stream(transformers).toList(), color);
    }

    public ExplosiveCatalystBehavior(Holder<ExplosiveCatalystHandler> handlerHolder, List<ExplosiveCatalystTransformer> transformers, int color) {
        this.handlerHolder = handlerHolder;
        this.transformers = transformers;
        this.color = color;
    }

    public void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld) {
        this.handlerHolder.value().createExplosion(context, detonationPosition, data, modifyWorld);
    }

    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData data) {
        for (ExplosiveCatalystTransformer transformer : this.transformers) {
            data = transformer.transformExplosiveCatalystData(context, data);
        }
        return data;
    }

    public boolean isNoOp() {
        return this.handlerHolder.equals(KlaxonExplosiveCatalystHandlers.NO_OP);
    }

    public final boolean is(ExplosiveCatalystBehavior behavior) {
        return this == behavior;
    }

    public final boolean is(Holder<ExplosiveCatalystBehavior> behaviorHolder) {
        return this.is(behaviorHolder.value());
    }
}