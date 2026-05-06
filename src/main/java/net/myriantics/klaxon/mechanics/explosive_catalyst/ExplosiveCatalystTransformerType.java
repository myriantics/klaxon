package net.myriantics.klaxon.mechanics.explosive_catalyst;

import com.mojang.serialization.MapCodec;

public record ExplosiveCatalystTransformerType<T extends ExplosiveCatalystTransformer>(MapCodec<T> codec) {
}
