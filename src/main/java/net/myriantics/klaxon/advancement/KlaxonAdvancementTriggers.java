package net.myriantics.klaxon.advancement;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class KlaxonAdvancementTriggers {
    public static void triggerAnvilRepair(ServerPlayerEntity serverPlayer, ItemStack stack) {
        KlaxonAdvancementCriteria.ANVIL_REPAIR_CRITERION.trigger(serverPlayer, stack);
    }
    public static void triggerBlockActivation(ServerPlayerEntity serverPlayer, BlockState state) {
        KlaxonAdvancementCriteria.BLOCK_ACTIVATION_CRITERION.trigger(serverPlayer, state);
    }
}
