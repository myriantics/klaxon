package net.myriantics.klaxon.registry.block;

import net.minecraft.state.property.BooleanProperty;

public abstract class KlaxonBlockStateProperties {
    public static final BooleanProperty FUELED = BooleanProperty.of("fueled");
    public static final BooleanProperty HATCH_OPEN = BooleanProperty.of("hatch_open");
    public static final BooleanProperty GROWTH_DISABLED = BooleanProperty.of("growth_disabled");
}
