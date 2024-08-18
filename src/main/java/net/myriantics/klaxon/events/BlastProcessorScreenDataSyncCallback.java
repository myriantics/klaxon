package net.myriantics.klaxon.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorOutputState;

public interface BlastProcessorScreenDataSyncCallback {

    Event<BlastProcessorScreenDataSyncCallback> EVENT = EventFactory.createArrayBacked(BlastProcessorScreenDataSyncCallback.class,
            (listeners) -> (explosionPower, explosionPowerMin, explosionPowerMax, producesFire, requiresFire, outputState) -> {
        for (BlastProcessorScreenDataSyncCallback listener : listeners) {
            listener.receivePowerData(explosionPower, explosionPowerMin, explosionPowerMax, producesFire, requiresFire, outputState);
        }
    });

    void receivePowerData(double explosionPower, double explosionPowerMin, double explosionPowerMax, boolean producesFire, boolean requiresFire, BlastProcessorOutputState outputState);
}
