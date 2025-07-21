package net.myriantics.klaxon.registry.block;

import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;

public abstract class KlaxonBlockFamilies {
    public static final BlockFamily HALLNOX = BlockFamilies.register(KlaxonBlocks.HALLNOX_PLANKS)
            .button(KlaxonBlocks.HALLNOX_BUTTON)
            .pressurePlate(KlaxonBlocks.HALLNOX_PRESSURE_PLATE)
            .fence(KlaxonBlocks.HALLNOX_FENCE)
            .fenceGate(KlaxonBlocks.HALLNOX_FENCE_GATE)
            .sign(KlaxonBlocks.HALLNOX_SIGN, KlaxonBlocks.HALLNOX_WALL_SIGN)
            .slab(KlaxonBlocks.HALLNOX_SLAB)
            .stairs(KlaxonBlocks.HALLNOX_STAIRS)
            .door(KlaxonBlocks.HALLNOX_DOOR)
            .trapdoor(KlaxonBlocks.HALLNOX_TRAPDOOR)
            .group("wooden")
            .unlockCriterionName("has_planks")
            .build();

}
