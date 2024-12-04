package net.myriantics.klaxon.block;

import net.minecraft.block.FacingBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;

public class KlaxonBlockStateProperties {
    public static final BooleanProperty FUELED = BooleanProperty.of("fueled");
    public static final BooleanProperty HATCH_OPEN = BooleanProperty.of("hatch_open");
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
}
