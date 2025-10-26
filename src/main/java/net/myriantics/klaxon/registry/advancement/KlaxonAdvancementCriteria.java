package net.myriantics.klaxon.registry.advancement;

import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.advancement.criterion.*;

public abstract class KlaxonAdvancementCriteria {

    public static final BlockActivationCriterion BLOCK_ACTIVATION_CRITERION = register("block_activation", new BlockActivationCriterion());
    public static final ItemRepairCriterion ANVIL_REPAIR_CRITERION = register("makeshift_equipment_repair", new ItemRepairCriterion());
    public static final WalljumpAbilityCriterion WALLJUMP_ABILITY_CRITERION = register("walljump_ability", new WalljumpAbilityCriterion());
    public static final ToolUsageRecipeCraftCriterion TOOL_USAGE_RECIPE_CRITERION = register("tool_usage_recipe", new ToolUsageRecipeCraftCriterion());
    public static final WrenchUsageCriterion WRENCH_USAGE_CRITERION = register("wrench_usage", new WrenchUsageCriterion());
    public static final InstabreakToolInstabreakCriterion INSTABREAK_TOOL_INSTABREAK_CRITERION = register("instabreak_tool_instabreak", new InstabreakToolInstabreakCriterion());
    public static final GrappleWinchVeinMineCriterion GRAPPLE_WINCH_VEIN_MINE_CRITERION = register("grapple_winch_veinmine", new GrappleWinchVeinMineCriterion());
    public static final EntityGrappleCriterion ENTITY_GRAPPLE_CRITERION = register("entity_grapple", new EntityGrappleCriterion());
    public static final GrappleWinchCableDisconnectCriterion GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION = register("grapple_winch_cable_disconnect_criterion", new GrappleWinchCableDisconnectCriterion());
    public static final OneOffCriterion DE_ANCHOR_GRAPPLE_WINCH_CLAW_CRITERION = register("de_anchor_grapple_winch_claw", new OneOffCriterion());

    private static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, KlaxonCommon.locate(name), criterion);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Advancement Criteria!");
    }
}
