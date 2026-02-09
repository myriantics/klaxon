package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.util.ItemActionResult;

public interface Wrenchable {
    ItemActionResult onManualWrenchInteraction(ManualWrenchInteractionContext context);

    boolean onDispenserWrenchInteraction(DispenserWrenchInteractionContext context);
}
