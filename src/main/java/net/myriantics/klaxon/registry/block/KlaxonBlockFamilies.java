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

    public static final BlockFamily STEEL = BlockFamilies.register(KlaxonBlocks.STEEL_BLOCK)
            .door(KlaxonBlocks.STEEL_DOOR)
            .trapdoor(KlaxonBlocks.STEEL_TRAPDOOR)
            .group("metal")
            .unlockCriterionName("has_block")
            .build();

    public static final BlockFamily CRUDE_STEEL = BlockFamilies.register(KlaxonBlocks.CRUDE_STEEL_BLOCK)
            .door(KlaxonBlocks.CRUDE_STEEL_DOOR)
            .trapdoor(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR)
            .group("metal")
            .unlockCriterionName("has_block")
            .build();
}
