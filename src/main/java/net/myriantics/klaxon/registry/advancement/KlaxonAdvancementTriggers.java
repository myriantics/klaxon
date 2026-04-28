package net.myriantics.klaxon.registry.advancement;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.muffling.MufflerActionType;
import net.myriantics.klaxon.mechanics.wrench.WrenchUsageType;

public abstract class KlaxonAdvancementTriggers {
    public static void triggerItemRepair(ServerPlayer serverPlayer, ItemStack stack) {
        KlaxonAdvancementCriteria.ANVIL_REPAIR_CRITERION.value().trigger(serverPlayer, stack);
    }
    public static void triggerBlockActivation(ServerPlayer serverPlayer, BlockState state) {
        KlaxonAdvancementCriteria.BLOCK_ACTIVATION_CRITERION.value().trigger(serverPlayer, state);
    }
    public static void triggerWalljumpAbility(ServerPlayer serverPlayer, HammerItem.UsageType usageType) {
        KlaxonAdvancementCriteria.WALLJUMP_ABILITY_CRITERION.value().trigger(serverPlayer, usageType);
    }
    public static void triggerToolUsageCraft(ServerPlayer serverPlayer, ItemStack toolStack, ItemStack craftedStack) {
        KlaxonAdvancementCriteria.TOOL_USAGE_RECIPE_CRITERION.value().trigger(serverPlayer, toolStack, craftedStack);
    }
    public static void triggerWrenchUsage(ServerPlayer serverPlayer, BlockPos pos, WrenchUsageType type) {
        KlaxonAdvancementCriteria.WRENCH_USAGE.value().trigger(serverPlayer, pos, type);
    }
    public static void triggerInstabreakToolInstabreak(ServerPlayer serverPlayer, ItemStack instabreakingTool, BlockState instabrokenState) {
        KlaxonAdvancementCriteria.INSTABREAK_TOOL_INSTABREAK_CRITERION.value().trigger(serverPlayer, instabreakingTool, instabrokenState);
    }
    public static void triggerGrappleWinchVeinMine(ServerPlayer serverPlayer, BlockState veinMinedState) {
        KlaxonAdvancementCriteria.GRAPPLE_WINCH_VEIN_MINE_CRITERION.value().trigger(serverPlayer, veinMinedState);
    }
    public static void triggerGrappleWinchIntentionallyDisconnectCable(ServerPlayer serverPlayer, GrapplingHook hook, CableDetachmentReason reason) {
        KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.value().trigger(serverPlayer, hook, reason);
    }
    public static void triggerGrappleWinchDeAnchorGrappleClaw(ServerPlayer serverPlayer) {
        KlaxonAdvancementCriteria.DE_ANCHOR_GRAPPLE_WINCH_CLAW_CRITERION.value().trigger(serverPlayer);
    }
    public static void triggerEntityGrapple(ServerPlayer serverPlayer, Entity grappledEntity) {
        KlaxonAdvancementCriteria.ENTITY_GRAPPLE_CRITERION.value().trigger(serverPlayer, grappledEntity);
    }

    public static void triggerGrappleWinchLevitationBug(ServerPlayer player) {
        KlaxonAdvancementCriteria.GRAPPLE_WINCH_LEVITATION_BUG_CRITERION.value().trigger(player);
    }

    public static void triggerErectFirewall(ServerPlayer player) {
        KlaxonAdvancementCriteria.IGNITE_MULTIPLE_FIRES_WITH_ONE_LIGHTER_USE.value().trigger(player);
    }

    public static void triggerMufflerInteraction(ServerPlayer serverPlayer, BlockPos muffledPos, MufflerActionType type, ItemStack appliedStack, ItemStack existingMufflerStack) {
        KlaxonAdvancementCriteria.MUFFLER_INTERACTION.value().trigger(serverPlayer, muffledPos, type, appliedStack, existingMufflerStack);
    }
}
