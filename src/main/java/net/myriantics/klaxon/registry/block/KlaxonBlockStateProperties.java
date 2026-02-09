package net.myriantics.klaxon.registry.block;

import net.minecraft.state.property.BooleanProperty;

public abstract class KlaxonBlockStateProperties {

    // Tracks whether the fuel slot of the Deepslate Blast Processor contains an item.
    public static final BooleanProperty FUELED = BooleanProperty.of("fueled");
    // Tracks whether the ingredient hatch of the Deepslate Blast Processor DOES NOT contain an item. Convoluted, I know.
    public static final BooleanProperty HATCH_OPEN = BooleanProperty.of("hatch_open");

    // Used by the Hallnox Pod to disable growth when interacted with using shears.
    public static final BooleanProperty GROWTH_DISABLED = BooleanProperty.of("growth_disabled");

    // Used by Pipe Matrices to indicate whether they're a valid structure for Geothermal Power Generation or not.
    public static final BooleanProperty FORMED = BooleanProperty.of("formed");
}
