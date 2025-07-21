package net.myriantics.klaxon.registry.minecraft;

import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.block.WoodType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonWoodTypes {
    public static final WoodType HALLNOX = new WoodTypeBuilder()
            .soundGroup(BlockSoundGroup.NETHER_WOOD)
            .fenceGateCloseSound(SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE)
            .fenceGateOpenSound(SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN)
            .hangingSignSoundGroup(BlockSoundGroup.NETHER_WOOD_HANGING_SIGN)
            .register(KlaxonCommon.locate("hallnox"), KlaxonBlockSetTypes.HALLNOX);
}
