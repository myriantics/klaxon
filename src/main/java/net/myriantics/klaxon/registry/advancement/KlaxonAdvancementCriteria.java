package net.myriantics.klaxon.registry.advancement;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.advancement.criterion.*;
import net.myriantics.klaxon.advancement.criterion.grapple_winch.EntityGrappleCriterion;
import net.myriantics.klaxon.advancement.criterion.grapple_winch.GrappleWinchCableDisconnectCriterion;
import net.myriantics.klaxon.advancement.criterion.grapple_winch.GrappleWinchVeinMineCriterion;

public abstract class KlaxonAdvancementCriteria {

    public static final Holder<BlockActivationCriterion> BLOCK_ACTIVATION_CRITERION = register("block_activation", new BlockActivationCriterion());
    public static final Holder<ItemRepairCriterion> ANVIL_REPAIR_CRITERION = register("makeshift_equipment_repair", new ItemRepairCriterion());
    public static final Holder<WalljumpAbilityCriterion> WALLJUMP_ABILITY_CRITERION = register("walljump_ability", new WalljumpAbilityCriterion());
    public static final Holder<ToolUsageRecipeCraftCriterion> TOOL_USAGE_RECIPE_CRITERION = register("tool_usage_recipe", new ToolUsageRecipeCraftCriterion());
    public static final Holder<WrenchUsageCriterion> WRENCH_USAGE_CRITERION = register("wrench_usage", new WrenchUsageCriterion());
    public static final Holder<InstabreakToolInstabreakCriterion> INSTABREAK_TOOL_INSTABREAK_CRITERION = register("instabreak_tool_instabreak", new InstabreakToolInstabreakCriterion());
    public static final Holder<GrappleWinchVeinMineCriterion> GRAPPLE_WINCH_VEIN_MINE_CRITERION = register("grapple_winch_veinmine", new GrappleWinchVeinMineCriterion());
    public static final Holder<EntityGrappleCriterion> ENTITY_GRAPPLE_CRITERION = register("entity_grapple", new EntityGrappleCriterion());
    public static final Holder<OneOffCriterion> GRAPPLE_WINCH_LEVITATION_BUG_CRITERION = register("grapple_winch_levitation_bug", new OneOffCriterion());
    public static final Holder<GrappleWinchCableDisconnectCriterion> GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION = register("grapple_winch_cable_disconnect_criterion", new GrappleWinchCableDisconnectCriterion());
    public static final Holder<OneOffCriterion> DE_ANCHOR_GRAPPLE_WINCH_CLAW_CRITERION = register("de_anchor_grapple_winch_claw", new OneOffCriterion());

    @SuppressWarnings("unchecked")
    private static <T extends CriterionTrigger<?>> Holder<T> register(String name, T criterion) {
        return (Holder<T>) Registry.registerForHolder(BuiltInRegistries.TRIGGER_TYPES, KlaxonCommon.locate(name), criterion);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Advancement Criteria!");
    }
}
