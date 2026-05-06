package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystHandler;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformerType;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionType;

import java.util.function.Consumer;

public abstract class KlaxonRegistries {
    public static final Registry<ExplosiveCatalystHandler> EXPLOSIVE_CATALYST_HANDLERS = register(
            KlaxonRegistryKeys.EXPLOSIVE_CATALYST_HANDLER,
            builder -> builder.attribute(RegistryAttribute.SYNCED)
    );
    public static final Registry<ExplosiveCatalystTransformerType<?>> EXPLOSIVE_CATALYST_TRANSFORMER_TYPES = register(
            KlaxonRegistryKeys.EXPLOSIVE_CATALYST_TRANSFORMER_TYPE,
            builder -> builder.attribute(RegistryAttribute.SYNCED)
    );
    public static final Registry<BlockStateWrenchBehavior<? extends Comparable<?>>> BLOCK_STATE_WRENCH_BEHAVIORS = register(
            KlaxonRegistryKeys.BLOCK_STATE_WRENCH_BEHAVIOR,
            (builder) -> builder.attribute(RegistryAttribute.SYNCED)
    );
    public static final Registry<WrenchActionType> WRENCH_ACTION_TYPE = register(
            KlaxonRegistryKeys.WRENCH_ACTION_TYPE,
            (builder) -> builder.attribute(RegistryAttribute.SYNCED)
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Registries!");
    }

    private static <T> Registry<T> register(ResourceKey<Registry<T>> key, Consumer<FabricRegistryBuilder<T, MappedRegistry<T>>> builderConsumer) {
        FabricRegistryBuilder<T, MappedRegistry<T>> builder = FabricRegistryBuilder.createSimple(key);
        builderConsumer.accept(builder);
        return builder.buildAndRegister();
    }
}
