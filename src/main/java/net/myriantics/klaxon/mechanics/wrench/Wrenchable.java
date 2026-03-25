package net.myriantics.klaxon.mechanics.wrench;

import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;

public interface Wrenchable {

    WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context);

    WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context);
}
