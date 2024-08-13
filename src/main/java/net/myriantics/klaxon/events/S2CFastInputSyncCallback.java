package net.myriantics.klaxon.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface S2CFastInputSyncCallback {
    Event<S2CFastInputSyncCallback> EVENT = EventFactory.createArrayBacked(S2CFastInputSyncCallback.class,
            listeners -> (player, world, pos) -> {
        for (S2CFastInputSyncCallback listener : listeners) {
            ActionResult result = listener.interact(player, world, pos);

            if (result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
    });

    ActionResult interact(PlayerEntity player, World world, BlockPos pos);
}
