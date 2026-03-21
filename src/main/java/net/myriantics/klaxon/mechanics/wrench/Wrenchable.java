package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.world.ItemInteractionResult;

public interface Wrenchable {
    ItemInteractionResult onManualWrenchInteraction(ManualWrenchInteractionContext context);

    boolean onDispenserWrenchInteraction(DispenserWrenchInteractionContext context);
}
