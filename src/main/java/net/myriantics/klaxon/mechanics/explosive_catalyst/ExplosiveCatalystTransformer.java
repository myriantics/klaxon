package net.myriantics.klaxon.mechanics.explosive_catalyst;

import com.mojang.serialization.Codec;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.KlaxonRegistries;

import java.util.List;

public abstract class ExplosiveCatalystTransformer {

    public static final Codec<ExplosiveCatalystTransformer> CODEC = KlaxonRegistries.EXPLOSIVE_CATALYST_TRANSFORMER_TYPES
            .byNameCodec()
            .dispatch("transformer", ExplosiveCatalystTransformer::getType, ExplosiveCatalystTransformerType::codec);
    public static final Codec<List<ExplosiveCatalystTransformer>> LIST_CODEC = Codec.list(CODEC);

    public abstract ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original);

    public abstract ExplosiveCatalystTransformerType<? extends ExplosiveCatalystTransformer> getType();
}
