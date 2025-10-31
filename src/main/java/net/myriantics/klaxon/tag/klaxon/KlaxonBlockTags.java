package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonBlockTags {

    // mining-related tags
    public static final TagKey<Block> HAMMER_MINEABLE =
            createMineableTag("hammer");
    public static final TagKey<Block> HAMMER_INSTABREAKABLE =
            createInstabreakableTag("hammer");
    public static final TagKey<Block> CABLE_SHEARS_MINEABLE =
            createMineableTag("cable_shears");
    public static final TagKey<Block> CABLE_SHEARS_INSTABREAKABLE =
            createInstabreakableTag("cable_shears");
    public static final TagKey<Block> CLEAVER_MINEABLE =
            createMineableTag("cleaver");
    public static final TagKey<Block> CLEAVER_INSTABREAKABLE =
            createInstabreakableTag("cleaver");
    public static final TagKey<Block> WRENCH_MINEABLE =
            createMineableTag("wrench");
    public static final TagKey<Block> WRENCH_INSTABREAKABLE =
            createInstabreakableTag("wrench");

    // tool material tags
    public static final TagKey<Block> NEEDS_STEEL_TOOL =
            createTag("needs_steel_tool");
    public static final TagKey<Block> INCORRECT_FOR_STEEL_TOOL =
            createTag("incorrect_for_steel_tool");

    // behavior tags
    public static final TagKey<Block> GRAPPLE_CLAW_BREAKABLE =
            createTag("grapple_claw_breakable");
    public static final TagKey<Block> GRAPPLE_CLAW_VEINMINEABLE =
            createTag("grapple_claw_veinmineable");
    public static final TagKey<Block> WRENCH_PICKUP_ALLOWLIST =
            createTag("wrench_pickup_allowlist");
    public static final TagKey<Block> WRENCH_PICKUP_DENYLIST =
            createTag("wrench_pickup_denylist");
    public static final TagKey<Block> WRENCH_INTERACTION_GENERAL_DENYLIST =
            createTag("wrench_interaction_general_denylist");
    public static final TagKey<Block> FERROMAGNETIC_BLOCKS =
            createTag("ferromagnetic_blocks");
    public static final TagKey<Block> COLD_BLOCKS =
            createTag("cold_blocks");
    public static final TagKey<Block> HALLNOX_POD_NATURAL_GROWTH_INHIBITING =
            createTag("hallnox_pod_natural_growth_inhibiting");
    public static final TagKey<Block> HEAVY_FALLING_BLOCKS =
            createTag("heavy_falling_blocks");

    // nether reaction tags
    public static final TagKey<Block> NETHER_REACTION_IMMUNE =
            createTag("nether_reaction_immune");

    public static final TagKey<Block> STEEL_CASING_CONVERTIBLE =
            createNetherReactionTag("steel_casing");

    public static final TagKey<Block> AIR_CONVERTIBLE =
            createNetherReactionTag("air");
    public static final TagKey<Block> FIRE_CONVERTIBLE =
            createNetherReactionTag("fire");
    public static final TagKey<Block> SHROOMLIGHT_CONVERTIBLE =
            createNetherReactionTag("shroomlight");
    public static final TagKey<Block> BONE_BLOCK_CONVERTIBLE =
            createNetherReactionTag("bone_block");
    public static final TagKey<Block> WITHER_SKELETON_SKULL_CONVERTIBLE =
            createNetherReactionTag("wither_skeleton_skull");
    public static final TagKey<Block> WITHER_SKELETON_WALL_SKULL_CONVERTIBLE =
            createNetherReactionTag("wither_skeleton_wall_skull");
    public static final TagKey<Block> NETHER_QUARTZ_ORE_CONVERTIBLE =
            createNetherReactionTag("nether_quartz_ore");
    public static final TagKey<Block> NETHER_GOLD_ORE_CONVERTIBLE =
            createNetherReactionTag("nether_gold_ore");

    public static final TagKey<Block> NETHERRACK_CONVERTIBLE =
            createNetherReactionTag("netherrack");

    public static final TagKey<Block> BLACKSTONE_CONVERTIBLE =
            createNetherReactionTag("blackstone");
    public static final TagKey<Block> BLACKSTONE_STAIRS_CONVERTIBLE =
            createNetherReactionTag("blackstone_stairs");
    public static final TagKey<Block> BLACKSTONE_SLAB_CONVERTIBLE =
            createNetherReactionTag("blackstone_slab");
    public static final TagKey<Block> BLACKSTONE_WALL_CONVERTIBLE =
            createNetherReactionTag("blackstone_wall");

    public static final TagKey<Block> BASALT_CONVERTIBLE =
            createNetherReactionTag("basalt");

    public static final TagKey<Block> SOUL_SAND_CONVERTIBLE =
            createNetherReactionTag("soul_sand");
    public static final TagKey<Block> SOUL_SOIL_CONVERTIBLE =
            createNetherReactionTag("soul_soil");
    public static final TagKey<Block> SOUL_TORCH_CONVERTIBLE =
            createNetherReactionTag("soul_torch");
    public static final TagKey<Block> SOUL_WALL_TORCH_CONVERTIBLE =
            createNetherReactionTag("soul_wall_torch");
    public static final TagKey<Block> SOUL_LANTERN_CONVERTIBLE =
            createNetherReactionTag("soul_lantern");
    public static final TagKey<Block> SOUL_CAMPFIRE_CONVERTIBLE =
            createNetherReactionTag("soul_campfire");

    public static final TagKey<Block> CRIMSON_NYLIUM_CONVERTIBLE =
            createNetherReactionTag("crimson_nylium");
    public static final TagKey<Block> CRIMSON_FUNGUS_CONVERTIBLE =
            createNetherReactionTag("crimson_fungus");
    public static final TagKey<Block> CRIMSON_ROOTS_CONVERTIBLE =
            createNetherReactionTag("crimson_roots");
    public static final TagKey<Block> NETHER_WART_BLOCK_CONVERTIBLE =
            createNetherReactionTag("nether_wart_block");
    public static final TagKey<Block> WEEPING_VINE_CONVERTIBLE =
            createNetherReactionTag("weeping_vine");
    public static final TagKey<Block> CRIMSON_STEM_CONVERTIBLE =
            createNetherReactionTag("crimson_stem");
    public static final TagKey<Block> STRIPPED_CRIMSON_STEM_CONVERTIBLE =
            createNetherReactionTag("stripped_crimson_stem");
    public static final TagKey<Block> CRIMSON_HYPHAE_CONVERTIBLE =
            createNetherReactionTag("crimson_hyphae");
    public static final TagKey<Block> STRIPPED_CRIMSON_HYPHAE_CONVERTIBLE =
            createNetherReactionTag("stripped_crimson_hyphae");
    public static final TagKey<Block> CRIMSON_PLANKS_CONVERTIBLE =
            createNetherReactionTag("crimson_planks");
    public static final TagKey<Block> CRIMSON_STAIRS_CONVERTIBLE =
            createNetherReactionTag("crimson_stairs");
    public static final TagKey<Block> CRIMSON_SLAB_CONVERTIBLE =
            createNetherReactionTag("crimson_slab");
    public static final TagKey<Block> CRIMSON_BUTTON_CONVERTIBLE =
            createNetherReactionTag("crimson_button");
    public static final TagKey<Block> CRIMSON_PRESSURE_PLATE_CONVERTIBLE =
            createNetherReactionTag("crimson_pressure_plate");
    public static final TagKey<Block> CRIMSON_FENCE_CONVERTIBLE =
            createNetherReactionTag("crimson_fence");
    public static final TagKey<Block> CRIMSON_FENCE_GATE_CONVERTIBLE =
            createNetherReactionTag("crimson_fence_gate");
    public static final TagKey<Block> CRIMSON_DOOR_CONVERTIBLE =
            createNetherReactionTag("crimson_door");
    public static final TagKey<Block> CRIMSON_TRAPDOOR_CONVERTIBLE =
            createNetherReactionTag("crimson_trapdoor");
    public static final TagKey<Block> CRIMSON_SIGN_CONVERTIBLE =
            createNetherReactionTag("crimson_sign");
    public static final TagKey<Block> CRIMSON_WALL_SIGN_CONVERTIBLE =
            createInstabreakableTag("crimson_wall_sign");
    public static final TagKey<Block> CRIMSON_HANGING_SIGN_CONVERTIBLE =
            createNetherReactionTag("crimson_hanging_sign");
    public static final TagKey<Block> CRIMSON_WALL_HANGING_SIGN_CONVERTIBLE =
            createNetherReactionTag("crimson_wall_hanging_sign");

    public static final TagKey<Block> WARPED_NYLIUM_CONVERTIBLE =
            createNetherReactionTag("warped_nylium");
    public static final TagKey<Block> WARPED_FUNGUS_CONVERTIBLE =
            createNetherReactionTag("warped_fungus");
    public static final TagKey<Block> TWISTING_VINES_CONVERTIBLE =
            createNetherReactionTag("twisting_vines");
    public static final TagKey<Block> WARPED_WART_BLOCK_CONVERTIBLE =
            createNetherReactionTag("warped_wart_block");
    public static final TagKey<Block> WARPED_STEM_CONVERTIBLE =
            createNetherReactionTag("warped_stem");

    // manual item application tags
    public static final TagKey<Block> NETHER_REACTOR_CORE_CONVERTIBLE =
            createManualItemApplicationTag("nether_reactor_core");
    public static final TagKey<Block> CRUDE_NETHER_REACTOR_CORE_CONVERTIBLE =
            createManualItemApplicationTag("crude_nether_reactor_core");

    // category tags
    public static final TagKey<Block> BLAST_PROCESSORS =
            createTag("blast_processors");
    public static final TagKey<Block> NETHER_REACTOR_CORES =
            createTag("nether_reactor_cores");
    public static final TagKey<Block> MACHINES =
            createTag("machines");
    public static final TagKey<Block> HALLNOX_STEMS =
            createTag("hallnox_stems");

    // plating blocks
    public static final TagKey<Block> PLATING_BLOCKS =
            createTag("plating_blocks");
    public static final TagKey<Block> STEEL_PLATING_BLOCKS =
            createPlatingBlockTag("steel");
    public static final TagKey<Block> CRUDE_STEEL_PLATING_BLOCKS =
            createPlatingBlockTag("crude_steel");
    public static final TagKey<Block> GOLD_PLATING_BLOCKS =
            createPlatingBlockTag("gold");
    public static final TagKey<Block> COPPER_PLATING_BLOCKS =
            createPlatingBlockTag("copper");
    public static final TagKey<Block> IRON_PLATING_BLOCKS =
            createPlatingBlockTag("iron");

    // wire spools
    public static final TagKey<Block> WIRE_SPOOLS =
            createTag("wire_spools");
    public static final TagKey<Block> COPPER_WIRE_SPOOLS =
            createWireSpoolsTag("copper");
    public static final TagKey<Block> IRON_WIRE_SPOOLS =
            createWireSpoolsTag("iron");
    public static final TagKey<Block> GOLD_WIRE_SPOOLS =
            createWireSpoolsTag("gold");
    public static final TagKey<Block> STEEL_WIRE_SPOOLS =
            createWireSpoolsTag("steel");

    // pipe matrices
    public static final TagKey<Block> PIPE_MATRICES =
            createTag("pipe_matrices");
    public static final TagKey<Block> PIPE_MATRIX_U_BENDS =
            createPipeMatricesTag("u_bends");
    public static final TagKey<Block> PIPE_MATRIX_SEGMENTS =
            createPipeMatricesTag("segments");
    public static final TagKey<Block> COPPER_PIPE_MATRICES =
            createPipeMatricesTag("copper");
    public static final TagKey<Block> COPPER_PIPE_MATRIX_U_BENDS =
            createPipeMatrixUBendTag("copper");
    public static final TagKey<Block> COPPER_PIPE_MATRIX_SEGMENTS =
            createPipeMatrixSegmentTag("copper");

    // advancement tags

    private static TagKey<Block> createInstabreakableTag(String name) {
        return createTag("instabreakable/" + name);
    }

    private static TagKey<Block> createMineableTag(String name) {
        return createTag("mineable/" + name);
    }

    private static TagKey<Block> createNetherReactionTag(String name) {
        return createTag("nether_reaction/" + name + "_convertible");
    }

    private static TagKey<Block> createPlatingBlockTag(String name) {
        return createTag("plating_block/" + name);
    }

    private static TagKey<Block> createManualItemApplicationTag(String name) {
        return createTag("manual_item_application/" + name + "_convertible");
    }

    private static TagKey<Block> createPipeMatricesTag(String name) {
        return createTag("pipe_matrices/" + name);
    }

    private static TagKey<Block> createPipeMatrixSegmentTag(String name) {
        return createPipeMatricesTag("segment/" + name);
    }

    private static TagKey<Block> createPipeMatrixUBendTag(String name) {
        return createPipeMatricesTag("u_bend/" + name);
    }

    private static TagKey<Block> createWireSpoolsTag(String name) {
        return createTag("wire_spools/" + name);
    }

    private static TagKey<Block> createTag(String name) {
        return TagKey.of(RegistryKeys.BLOCK, KlaxonCommon.locate(name));
    }
}
