package net.myriantics.klaxon.registry.block;

import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;

public abstract class KlaxonBlockFamilies {
    public static final BlockFamily HALLNOX = BlockFamilies.familyBuilder(KlaxonBlocks.HALLNOX_PLANKS)
            .button(KlaxonBlocks.HALLNOX_BUTTON)
            .pressurePlate(KlaxonBlocks.HALLNOX_PRESSURE_PLATE)
            .fence(KlaxonBlocks.HALLNOX_FENCE)
            .fenceGate(KlaxonBlocks.HALLNOX_FENCE_GATE)
            .sign(KlaxonBlocks.HALLNOX_SIGN, KlaxonBlocks.HALLNOX_WALL_SIGN)
            .slab(KlaxonBlocks.HALLNOX_SLAB)
            .stairs(KlaxonBlocks.HALLNOX_STAIRS)
            .door(KlaxonBlocks.HALLNOX_DOOR)
            .trapdoor(KlaxonBlocks.HALLNOX_TRAPDOOR)
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks")
            .getFamily();

    public static final BlockFamily STEEL = BlockFamilies.familyBuilder(KlaxonBlocks.STEEL_BLOCK)
            .door(KlaxonBlocks.STEEL_DOOR)
            .trapdoor(KlaxonBlocks.STEEL_TRAPDOOR)
            .recipeGroupPrefix("metal")
            .recipeUnlockedBy("has_block")
            .getFamily();

    public static final BlockFamily CRUDE_STEEL = BlockFamilies.familyBuilder(KlaxonBlocks.CRUDE_STEEL_BLOCK)
            .door(KlaxonBlocks.CRUDE_STEEL_DOOR)
            .trapdoor(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR)
            .recipeGroupPrefix("metal")
            .recipeUnlockedBy("has_block")
            .getFamily();
}
