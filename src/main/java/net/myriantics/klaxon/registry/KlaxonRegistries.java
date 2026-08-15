package net.myriantics.klaxon.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystHandler;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformerType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.definition.ExplosiveCatalystDefinition;
import net.myriantics.klaxon.mechanics.grapple_winch.VeinmineGroup;
import net.myriantics.klaxon.mechanics.turbine_generator.power_source.StaticTurbineGeneratorPowerSource;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionType;
import net.myriantics.klaxon.mechanics.wrench.WrenchInteractionDenialPredicate;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;

public abstract class KlaxonRegistries {

    // static
    public static final ResourceKey<Registry<BlockStateWrenchBehavior<? extends Comparable<?>>>> BLOCK_STATE_WRENCH_BEHAVIOR = of("block_state_wrench_behavior");
    public static final ResourceKey<Registry<WrenchActionType>> WRENCH_ACTION_TYPE = of("wrench_action_type");
    public static final ResourceKey<Registry<ExplosiveCatalystHandler>> EXPLOSIVE_CATALYST_HANDLER = of("explosive_catalyst_handler");
    public static final ResourceKey<Registry<ExplosiveCatalystTransformerType<?>>> EXPLOSIVE_CATALYST_TRANSFORMER_TYPE = of("explosive_catalyst_transformer_type");
    // dynamic
    public static final ResourceKey<Registry<ExplosiveCatalystBehavior>> EXPLOSIVE_CATALYST_BEHAVIOR = of("blast_processor_behavior");
    public static final ResourceKey<Registry<WrenchInteractionDenialPredicate>> WRENCH_INTERACTION_DENIAL_PREDICATE = of("wrench_interaction_denial_predicates");
    public static final ResourceKey<Registry<ToolUsageRecipeType>> TOOL_USAGE_RECIPE_TYPE = of("tool_usage_recipe_type");
    public static final ResourceKey<Registry<VeinmineGroup>> VEINMINE_GROUP = of("veinmine_group");
    public static final ResourceKey<Registry<ExplosiveCatalystDefinition>> EXPLOSIVE_CATALYST_DEFINITION = of("explosive_catalyst_definition");
    public static final ResourceKey<Registry<StaticTurbineGeneratorPowerSource>> STATIC_TURBINE_GENERATOR_POWER_SOURCE = of("turbine_generator_power_source");

    private static <T> ResourceKey<Registry<T>> of(String id) {
        return ResourceKey.createRegistryKey(KlaxonCommon.locate(id));
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Registry Keys!");
    }
}
