package net.myriantics.klaxon.registry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;

public abstract class KlaxonRegistryKeys {
    
    public static final RegistryKey<Registry<ExplosiveCatalystBehavior>> EXPLOSIVE_CATALYST_BEHAVIOR = of("blast_processor_behavior");
    public static final RegistryKey<Registry<BlockStateWrenchBehavior<? extends Comparable<?>>>> BLOCK_STATE_WRENCH_BEHAVIOR = of("block_state_wrench_behavior");
    public static final RegistryKey<Registry<ToolUsageRecipeType>> TOOL_USAGE_RECIPE_TYPE = of("tool_usage_recipe_type");

    private static <T> RegistryKey<Registry<T>> of(String id) {
        return RegistryKey.ofRegistry(KlaxonCommon.locate(id));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Registry Keys!");
    }
}
