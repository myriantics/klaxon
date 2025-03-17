package net.myriantics.klaxon.registry;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;

public abstract class KlaxonAdvancementTriggers {
    public static void triggerAnvilRepair(ServerPlayerEntity serverPlayer, ItemStack stack) {
        KlaxonAdvancementCriteria.ANVIL_REPAIR_CRITERION.trigger(serverPlayer, stack);
    }
    public static void triggerBlockActivation(ServerPlayerEntity serverPlayer, BlockPos pos) {
        KlaxonAdvancementCriteria.BLOCK_ACTIVATION_CRITERION.trigger(serverPlayer, pos);
    }
    public static void triggerHammerUse(ServerPlayerEntity serverPlayer, HammerItem.UsageType usageType) {
        KlaxonAdvancementCriteria.HAMMER_USE_CRITERION.trigger(serverPlayer, usageType);
    }
}
