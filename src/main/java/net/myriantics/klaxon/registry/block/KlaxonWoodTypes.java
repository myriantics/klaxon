package net.myriantics.klaxon.registry.block;

import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonWoodTypes {
    public static final WoodType HALLNOX = new WoodTypeBuilder()
            .soundGroup(SoundType.NETHER_WOOD)
            .fenceGateCloseSound(SoundEvents.NETHER_WOOD_FENCE_GATE_CLOSE)
            .fenceGateOpenSound(SoundEvents.NETHER_WOOD_FENCE_GATE_OPEN)
            .hangingSignSoundGroup(SoundType.NETHER_WOOD_HANGING_SIGN)
            .register(KlaxonCommon.locate("hallnox"), KlaxonBlockSetTypes.HALLNOX);
}
