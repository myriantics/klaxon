package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.*;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;

import java.util.function.Consumer;

public abstract class KlaxonRegistries {
    public static final Registry<ExplosiveCatalystBehavior> EXPLOSIVE_CATALYST_BEHAVIORS = register(
            KlaxonRegistryKeys.EXPLOSIVE_CATALYST_BEHAVIOR,
            (builder) -> builder.attribute(RegistryAttribute.SYNCED)
    );
    public static final Registry<BlockStateWrenchBehavior<? extends Comparable<?>>> BLOCK_STATE_WRENCH_BEHAVIORS = register(
            KlaxonRegistryKeys.BLOCK_STATE_WRENCH_BEHAVIOR,
            (builder) -> builder.attribute(RegistryAttribute.SYNCED)
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Registries!");
    }

    private static <T> Registry<T> register(RegistryKey<Registry<T>> key, Consumer<FabricRegistryBuilder<T, SimpleRegistry<T>>> builderConsumer) {
        FabricRegistryBuilder<T, SimpleRegistry<T>> builder = FabricRegistryBuilder.createSimple(key);
        builderConsumer.accept(builder);
        return builder.buildAndRegister();
    }
}
