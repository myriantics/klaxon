package net.myriantics.klaxon.registry.advancement;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;

public abstract class KlaxonAdvancementTriggers {
    public static void triggerItemRepair(ServerPlayerEntity serverPlayer, ItemStack stack) {
        KlaxonAdvancementCriteria.ANVIL_REPAIR_CRITERION.trigger(serverPlayer, stack);
    }
    public static void triggerBlockActivation(ServerPlayerEntity serverPlayer, BlockState state) {
        KlaxonAdvancementCriteria.BLOCK_ACTIVATION_CRITERION.trigger(serverPlayer, state);
    }
    public static void triggerWalljumpAbility(ServerPlayerEntity serverPlayer, HammerItem.UsageType usageType) {
        KlaxonAdvancementCriteria.WALLJUMP_ABILITY_CRITERION.trigger(serverPlayer, usageType);
    }
    public static void triggerToolUsageCraft(ServerPlayerEntity serverPlayer, ItemStack toolStack, ItemStack craftedStack) {
        KlaxonAdvancementCriteria.TOOL_USAGE_RECIPE_CRITERION.trigger(serverPlayer, toolStack, craftedStack);
    }
    public static void triggerWrenchUsage(ServerPlayerEntity serverPlayer, WrenchItem.UsageType usageType, BlockState targetState) {
        KlaxonAdvancementCriteria.WRENCH_USAGE_CRITERION.trigger(serverPlayer, usageType, targetState);
    }
    public static void triggerInstabreakToolInstabreak(ServerPlayerEntity serverPlayer, ItemStack instabreakingTool, BlockState instabrokenState) {
        KlaxonAdvancementCriteria.INSTABREAK_TOOL_INSTABREAK_CRITERION.trigger(serverPlayer, instabreakingTool, instabrokenState);
    }
    public static void triggerGrappleWinchVeinMine(ServerPlayerEntity serverPlayer, BlockState veinMinedState) {
        KlaxonAdvancementCriteria.GRAPPLE_WINCH_VEIN_MINE_CRITERION.trigger(serverPlayer, veinMinedState);
    }
    public static void triggerGrappleWinchIntentionallyDisconnectCable(ServerPlayerEntity serverPlayer, GrapplingHook hook, CableDetachmentReason reason) {
        KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.trigger(serverPlayer, hook, reason);
    }
    public static void triggerGrappleWinchDeAnchorGrappleClaw(ServerPlayerEntity serverPlayer) {
        KlaxonAdvancementCriteria.DE_ANCHOR_GRAPPLE_WINCH_CLAW_CRITERION.trigger(serverPlayer);
    }
    public static void triggerEntityGrapple(ServerPlayerEntity serverPlayer, Entity grappledEntity) {
        KlaxonAdvancementCriteria.ENTITY_GRAPPLE_CRITERION.trigger(serverPlayer, grappledEntity);
    }
}
