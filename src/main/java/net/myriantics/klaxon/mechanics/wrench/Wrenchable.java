package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;

public interface Wrenchable {

    default Direction getManualInteractedFace(BlockState state, Direction clickedFace, Vec3 clickedPosFromMiddle) {
        return clickedFace;
    }

    WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context);

    WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context);
}
