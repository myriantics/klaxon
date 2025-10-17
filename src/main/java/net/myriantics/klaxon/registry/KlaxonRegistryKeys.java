package net.myriantics.klaxon.registry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.BlastProcessorCatalystBehavior;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;

public abstract class KlaxonRegistryKeys {
    
    public static final RegistryKey<Registry<BlastProcessorCatalystBehavior>> BLAST_PROCESSOR_BEHAVIORS = of("blast_processor_behaviors");
    public static final RegistryKey<Registry<ToolUsageRecipeType>> TOOL_USAGE_RECIPE_TYPE = of("tool_usage_recipe_type");

    private static <T> RegistryKey<Registry<T>> of(String id) {
        return RegistryKey.ofRegistry(KlaxonCommon.locate(id));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Registry Keys!");
    }
}
