package net.myriantics.klaxon.registry.advancement;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;

public abstract class KlaxonAdvancementTriggers {
    public static void triggerItemRepair(ServerPlayer serverPlayer, ItemStack stack) {
        KlaxonAdvancementCriteria.ANVIL_REPAIR_CRITERION.trigger(serverPlayer, stack);
    }
    public static void triggerBlockActivation(ServerPlayer serverPlayer, BlockState state) {
        KlaxonAdvancementCriteria.BLOCK_ACTIVATION_CRITERION.trigger(serverPlayer, state);
    }
    public static void triggerWalljumpAbility(ServerPlayer serverPlayer, HammerItem.UsageType usageType) {
        KlaxonAdvancementCriteria.WALLJUMP_ABILITY_CRITERION.trigger(serverPlayer, usageType);
    }
    public static void triggerToolUsageCraft(ServerPlayer serverPlayer, ItemStack toolStack, ItemStack craftedStack) {
        KlaxonAdvancementCriteria.TOOL_USAGE_RECIPE_CRITERION.trigger(serverPlayer, toolStack, craftedStack);
    }
    public static void triggerWrenchUsage(ServerPlayer serverPlayer, WrenchItem.UsageType usageType, BlockState targetState) {
        KlaxonAdvancementCriteria.WRENCH_USAGE_CRITERION.trigger(serverPlayer, usageType, targetState);
    }
    public static void triggerInstabreakToolInstabreak(ServerPlayer serverPlayer, ItemStack instabreakingTool, BlockState instabrokenState) {
        KlaxonAdvancementCriteria.INSTABREAK_TOOL_INSTABREAK_CRITERION.trigger(serverPlayer, instabreakingTool, instabrokenState);
    }
    public static void triggerGrappleWinchVeinMine(ServerPlayer serverPlayer, BlockState veinMinedState) {
        KlaxonAdvancementCriteria.GRAPPLE_WINCH_VEIN_MINE_CRITERION.trigger(serverPlayer, veinMinedState);
    }
    public static void triggerGrappleWinchIntentionallyDisconnectCable(ServerPlayer serverPlayer, GrapplingHook hook, CableDetachmentReason reason) {
        KlaxonAdvancementCriteria.GRAPPLE_WINCH_CABLE_DISCONNECT_CRITERION.trigger(serverPlayer, hook, reason);
    }
    public static void triggerGrappleWinchDeAnchorGrappleClaw(ServerPlayer serverPlayer) {
        KlaxonAdvancementCriteria.DE_ANCHOR_GRAPPLE_WINCH_CLAW_CRITERION.trigger(serverPlayer);
    }
    public static void triggerEntityGrapple(ServerPlayer serverPlayer, Entity grappledEntity) {
        KlaxonAdvancementCriteria.ENTITY_GRAPPLE_CRITERION.trigger(serverPlayer, grappledEntity);
    }
}
