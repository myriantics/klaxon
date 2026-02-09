package net.myriantics.klaxon.mechanics.gerald_sniffer;

import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.util.StringIdentifiable;

public enum GeraldSnifferState implements StringIdentifiable {
    TRACKING_UNSUPPORTED("tracking_unsupported"),
    TRACKING_READY("tracking_ready"),
    TRACKING_IN_PROGRESS("tracking_in_progress"),
    TRACKING_FINISHED("tracking_finished");

    private final String id;

    GeraldSnifferState(String id) {
        this.id = id;
    }

    public boolean isReadyToStartTracking() {
        return this.equals(TRACKING_READY);
    }

    public boolean isActivelyTracking() {
        return this.equals(TRACKING_IN_PROGRESS);
    }

    @Override
    public String asString() {
        return this.id;
    }

    /**
     * Tries to parse the given id String into a GeraldSnifferState.
     * @param id The string to parse.
     * @return Returns parsed state if valid, with NOT_TRACKING as a fallback.
     */
    public static GeraldSnifferState fromString(String id) {
        if (id.equals(TRACKING_IN_PROGRESS.id)) {
            return TRACKING_IN_PROGRESS;
        }

        if (id.equals(TRACKING_FINISHED.id)) {
            return TRACKING_FINISHED;
        }

        return TRACKING_UNSUPPORTED;
    }
}
