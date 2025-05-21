package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;

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
}
