package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.world.InteractionResult;

public interface Wrenchable {
    InteractionResult onManualWrenchInteraction(ManualWrenchInteractionContext context);

    boolean onDispenserWrenchInteraction(DispenserWrenchInteractionContext context);
}
