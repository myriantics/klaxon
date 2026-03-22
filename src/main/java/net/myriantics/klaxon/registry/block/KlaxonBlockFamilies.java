package net.myriantics.klaxon.registry.block;

import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;

public abstract class KlaxonBlockFamilies {
    public static final BlockFamily HALLNOX = BlockFamilies.familyBuilder(KlaxonBlocks.HALLNOX_PLANKS.value())
            .button(KlaxonBlocks.HALLNOX_BUTTON.value())
            .pressurePlate(KlaxonBlocks.HALLNOX_PRESSURE_PLATE.value())
            .fence(KlaxonBlocks.HALLNOX_FENCE.value())
            .fenceGate(KlaxonBlocks.HALLNOX_FENCE_GATE.value())
            .sign(KlaxonBlocks.HALLNOX_SIGN.value(), KlaxonBlocks.HALLNOX_WALL_SIGN.value())
            .slab(KlaxonBlocks.HALLNOX_SLAB.value())
            .stairs(KlaxonBlocks.HALLNOX_STAIRS.value())
            .door(KlaxonBlocks.HALLNOX_DOOR.value())
            .trapdoor(KlaxonBlocks.HALLNOX_TRAPDOOR.value())
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks")
            .getFamily();

    public static final BlockFamily STEEL = BlockFamilies.familyBuilder(KlaxonBlocks.STEEL_BLOCK.value())
            .door(KlaxonBlocks.STEEL_DOOR.value())
            .trapdoor(KlaxonBlocks.STEEL_TRAPDOOR.value())
            .recipeGroupPrefix("metal")
            .recipeUnlockedBy("has_block")
            .getFamily();

    public static final BlockFamily CRUDE_STEEL = BlockFamilies.familyBuilder(KlaxonBlocks.CRUDE_STEEL_BLOCK.value())
            .door(KlaxonBlocks.CRUDE_STEEL_DOOR.value())
            .trapdoor(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR.value())
            .recipeGroupPrefix("metal")
            .recipeUnlockedBy("has_block")
            .getFamily();
}
