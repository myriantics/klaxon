package net.myriantics.klaxon.mechanics.explosive_catalyst;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystHandlers;

import java.util.Arrays;
import java.util.List;

public class ExplosiveCatalystBehavior {

    public final Holder<ExplosiveCatalystHandler> handlerHolder;
    public final List<ExplosiveCatalystTransformer> transformers;

    public static final Codec<ExplosiveCatalystBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            KlaxonRegistries.EXPLOSIVE_CATALYST_HANDLERS.holderByNameCodec().fieldOf("handler").forGetter(i -> i.handlerHolder),
            ExplosiveCatalystTransformer.LIST_CODEC.fieldOf("transformers").forGetter(i -> i.transformers)
    ).apply(instance, ExplosiveCatalystBehavior::new));

    public ExplosiveCatalystBehavior(Holder<ExplosiveCatalystHandler> handlerHolder, ExplosiveCatalystTransformer... transformers) {
        this(handlerHolder, Arrays.stream(transformers).toList());
    }

    public ExplosiveCatalystBehavior(Holder<ExplosiveCatalystHandler> handlerHolder, List<ExplosiveCatalystTransformer> transformers) {
        this.handlerHolder = handlerHolder;
        this.transformers = transformers;
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