package net.myriantics.klaxon.recipe.blast_processing;

import net.minecraft.util.StringIdentifiable;

public enum BlastProcessingOutputState implements StringIdentifiable {
    MISSING_RECIPE,
    MISSING_FUEL,
    UNDERPOWERED,
    OVERPOWERED,
    SUCCESS;

    @Override
    public String asString() {
        return this.name();
    }
}
