package net.myriantics.klaxon.registry.explosive_catalyst;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformer;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformerType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.transformer.*;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;

public abstract class KlaxonExplosiveCatalystTransformerTypes {

    public static final ExplosiveCatalystTransformerType<BaseBlockStateExplosiveCatalystTransformer> SUPPORTING_BLOCK_STATE = register("supporting_block_state", BaseBlockStateExplosiveCatalystTransformer.CODEC);
    public static final ExplosiveCatalystTransformerType<DimensionTypeDependentExplosiveCatalystTransformer> DIMENSION_TYPE_DEPENDENT = register("dimension_type_dependent", DimensionTypeDependentExplosiveCatalystTransformer.CODEC);
    public static final ExplosiveCatalystTransformerType<RedstoneSignalStrengthExplosiveCatalystTransformer> REDSTONE_SIGNAL_STRENGTH = register("redstone_signal_strength", RedstoneSignalStrengthExplosiveCatalystTransformer.CODEC);
    public static final ExplosiveCatalystTransformerType<FireworkExplosionExplosiveCatalystTransformer> FIREWORK_EXPLOSION = register("firework_explosion", FireworkExplosionExplosiveCatalystTransformer.CODEC);
    public static final ExplosiveCatalystTransformerType<FireworksExplosiveCatalystTransformer> FIREWORKS = register("fireworks", FireworksExplosiveCatalystTransformer.CODEC);

    private static <T extends ExplosiveCatalystTransformer> ExplosiveCatalystTransformerType<T> register(String name, MapCodec<T> codec) {
        return Registry.register(KlaxonBuiltInRegistries.EXPLOSIVE_CATALYST_TRANSFORMER_TYPES, KlaxonCommon.locate(name), new ExplosiveCatalystTransformerType<>(codec));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Explosive Catalyst Transformer Types!");
    }
}
