package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.myriantics.klaxon.registry.behavior.KlaxonBlockStateWrenchBehaviors;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.tag.compat.KlaxonCompatBlockTags;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public KlaxonBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        buildMiningTags();
        buildVeinmineGroupTags();
        buildBehaviorTags();
        buildWrenchTags();
        buildCategoricalTags();
        buildToolRequirementTags();
        buildCompatTags();
        buildNetherReactionTags();
        buildManualItemApplicationTags();
    }

    private void buildMiningTags() {
        // hammer
        getOrCreateTagBuilder(KlaxonBlockTags.HAMMER_MINEABLE)
                .forceAddTag(KlaxonBlockTags.HAMMER_INSTABREAKABLE)
                .add(Blocks.BEACON)
                .add(
                        Blocks.PACKED_ICE,
                        Blocks.BLUE_ICE
                );
        getOrCreateTagBuilder(KlaxonBlockTags.HAMMER_INSTABREAKABLE)
                .addOptionalTag(KlaxonCompatBlockTags.BRICK_BREAKABLE)
                .forceAddTag(ConventionalBlockTags.GLASS_BLOCKS)
                .forceAddTag(ConventionalBlockTags.GLASS_PANES)
                .forceAddTag(ConventionalBlockTags.BUDS)
                .forceAddTag(ConventionalBlockTags.CLUSTERS)
                .forceAddTag(ConventionalBlockTags.SKULLS)
                .add(KlaxonBlocks.HALLNOX_BULB.value())
                .add(
                        Blocks.ICE,
                        Blocks.FROSTED_ICE
                )
                .add(Blocks.REDSTONE_LAMP)
                .add(Blocks.SEA_LANTERN)
                .add(Blocks.GLOWSTONE)
                .add(Blocks.COCOA);

        // cable shears
        getOrCreateTagBuilder(KlaxonBlockTags.CABLE_SHEARS_MINEABLE)
                .forceAddTag(KlaxonBlockTags.CABLE_SHEARS_INSTABREAKABLE)
                .add(Blocks.COBWEB);
        getOrCreateTagBuilder(KlaxonBlockTags.CABLE_SHEARS_INSTABREAKABLE)
                .forceAddTag(KlaxonConventionalBlockTags.VINES)
                .forceAddTag(KlaxonConventionalBlockTags.GRATES)
                .forceAddTag(ConventionalBlockTags.CHAINS)
                .forceAddTag(ConventionalBlockTags.ROPES)
                .forceAddTag(KlaxonBlockTags.WIRE_SPOOLS)
                .forceAddTag(BlockTags.CEILING_HANGING_SIGNS)
                .forceAddTag(BlockTags.WOOL_CARPETS)
                .forceAddTag(BlockTags.WOOL)
                .forceAddTag(BlockTags.LEAVES)
                .add(
                        Blocks.CHORUS_PLANT,
                        Blocks.CHORUS_FLOWER
                )
                .add(Blocks.SPAWNER)
                .add(Blocks.IRON_BARS);

        // cleaver
        getOrCreateTagBuilder(KlaxonBlockTags.CLEAVER_MINEABLE)
                .forceAddTag(KlaxonBlockTags.CLEAVER_INSTABREAKABLE)
                .forceAddTag(BlockTags.MINEABLE_WITH_AXE)
                .add(Blocks.COBWEB);
        getOrCreateTagBuilder(KlaxonBlockTags.CLEAVER_INSTABREAKABLE)
                .forceAddTag(KlaxonConventionalBlockTags.VINES)
                .forceAddTag(ConventionalBlockTags.PUMPKINS)
                .forceAddTag(ConventionalBlockTags.ROPES)
                .forceAddTag(ConventionalBlockTags.SKULLS)
                .forceAddTag(BlockTags.CEILING_HANGING_SIGNS)
                .forceAddTag(BlockTags.WART_BLOCKS)
                .forceAddTag(BlockTags.CANDLE_CAKES)
                .forceAddTag(BlockTags.ALL_SIGNS)
                .forceAddTag(BlockTags.BEEHIVES)
                .forceAddTag(BlockTags.CANDLES)
                .forceAddTag(BlockTags.LEAVES)
                .add(
                        Blocks.SCULK_VEIN,
                        Blocks.SCULK,
                        Blocks.SCULK_CATALYST,
                        Blocks.SCULK_SENSOR,
                        Blocks.SCULK_SHRIEKER,
                        Blocks.CALIBRATED_SCULK_SENSOR
                )
                .add(
                        Blocks.CHORUS_FLOWER,
                        Blocks.CHORUS_PLANT
                )
                .add(
                        KlaxonBlocks.RUBBER_BLOCK.value(),
                        KlaxonBlocks.RUBBER_SHEET_BLOCK.value(),
                        KlaxonBlocks.MOLTEN_RUBBER_BLOCK.value()
                )
                .add(
                        Blocks.MUSHROOM_STEM,
                        Blocks.RED_MUSHROOM_BLOCK,
                        Blocks.BROWN_MUSHROOM_BLOCK
                )
                .add(
                        Blocks.OCHRE_FROGLIGHT,
                        Blocks.PEARLESCENT_FROGLIGHT,
                        Blocks.VERDANT_FROGLIGHT
                )
                .add(
                        Blocks.MOSS_BLOCK,
                        Blocks.MOSS_CARPET,
                        Blocks.AZALEA,
                        Blocks.FLOWERING_AZALEA
                )
                .add(KlaxonBlocks.HALLNOX_POD.value())
                .add(Blocks.COCOA)
                .add(Blocks.SHROOMLIGHT)
                .add(Blocks.MELON)
                .add(Blocks.BAMBOO)
                .add(Blocks.HONEYCOMB_BLOCK)
                .add(Blocks.GLOW_LICHEN)
                .add(Blocks.CAKE)
                .add(Blocks.CACTUS);

        // wrench
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_MINEABLE)
                .forceAddTag(KlaxonBlockTags.WRENCH_INSTABREAKABLE)
                .forceAddTag(BlockTags.TRAPDOORS)
                .forceAddTag(BlockTags.DOORS);
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_INSTABREAKABLE)
                .addOptionalTag(KlaxonCompatBlockTags.WRENCH_PICKUP)
                .forceAddTag(BlockTags.SHULKER_BOXES)
                .forceAddTag(BlockTags.RAILS)
                .forceAddTag(KlaxonConventionalBlockTags.METAL_DOORS)
                .forceAddTag(KlaxonConventionalBlockTags.METAL_TRAPDOORS)
                .forceAddTag(KlaxonConventionalBlockTags.SCAFFOLDINGS)
                .forceAddTag(KlaxonConventionalBlockTags.LEVERS)
                .forceAddTag(KlaxonBlockTags.PIPE_MATRICES)
                .add(
                        Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
                        Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,
                        KlaxonBlocks.HEAVY_GATED_PRESSURE_PLATE.value(),
                        KlaxonBlocks.FAULTY_HEAVY_GATED_PRESSURE_PLATE.value()
                )
                .add(
                        KlaxonBlocks.STEEL_CASING.value(),
                        KlaxonBlocks.CRUDE_STEEL_CASING.value()
                )
                .add(
                        KlaxonBlocks.NETHER_REACTOR_CORE.value(),
                        KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE.value()
                )
                .add(KlaxonBlocks.STEEL_BLAST_PROCESSOR.value())
                .add(KlaxonBlocks.PRECISION_DISPENSER.value())
                .add(KlaxonBlocks.STEEL_WORKBENCH.value())
                .add(
                        Blocks.COPPER_BULB,
                        Blocks.EXPOSED_COPPER_BULB,
                        Blocks.WEATHERED_COPPER_BULB,
                        Blocks.OXIDIZED_COPPER_BULB
                )
                .add(
                        Blocks.WAXED_COPPER_BULB,
                        Blocks.WAXED_EXPOSED_COPPER_BULB,
                        Blocks.WAXED_WEATHERED_COPPER_BULB,
                        Blocks.WAXED_OXIDIZED_COPPER_BULB
                )
                .add(
                        Blocks.STICKY_PISTON,
                        Blocks.PISTON,
                        Blocks.PISTON_HEAD
                )
                .add(KlaxonBlocks.HALLNOX_BULB.value())
                .add(Blocks.DAYLIGHT_DETECTOR)
                .add(Blocks.HOPPER)
                .add(Blocks.REDSTONE_LAMP)
                .add(Blocks.BLAST_FURNACE)
                .add(Blocks.OBSERVER)
                .add(Blocks.CRAFTER);

        // pickaxe
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .forceAddTag(KlaxonBlockTags.WIRE_SPOOLS)
                .forceAddTag(KlaxonBlockTags.PLATING_BLOCKS)
                .forceAddTag(KlaxonBlockTags.PIPE_MATRICES)
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.value())
                .add(
                        KlaxonBlocks.STEEL_BLOCK.value(),
                        KlaxonBlocks.STEEL_CASING.value(),
                        KlaxonBlocks.STEEL_DOOR.value(),
                        KlaxonBlocks.STEEL_TRAPDOOR.value(),
                        KlaxonBlocks.HEAVY_GATED_PRESSURE_PLATE.value()
                )
                .add(KlaxonBlocks.STEEL_WORKBENCH.value())
                .add(KlaxonBlocks.STEEL_BLAST_PROCESSOR.value())
                .add(
                        KlaxonBlocks.CRUDE_STEEL_BLOCK.value(),
                        KlaxonBlocks.CRUDE_STEEL_CASING.value(),
                        KlaxonBlocks.CRUDE_STEEL_DOOR.value(),
                        KlaxonBlocks.CRUDE_STEEL_TRAPDOOR.value(),
                        KlaxonBlocks.FAULTY_HEAVY_GATED_PRESSURE_PLATE.value()
                )
                .add(
                        KlaxonBlocks.NETHER_REACTOR_CORE.value(),
                        KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE.value()
                );

        // axe
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(
                        KlaxonBlocks.HALLNOX_POD.value(),
                        KlaxonBlocks.HALLNOX_STEM.value(),
                        KlaxonBlocks.STRIPPED_HALLNOX_STEM.value(),
                        KlaxonBlocks.HALLNOX_HYPHAE.value(),
                        KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE.value(),
                        KlaxonBlocks.HALLNOX_PLANKS.value(),
                        KlaxonBlocks.HALLNOX_STAIRS.value(),
                        KlaxonBlocks.HALLNOX_SLAB.value(),
                        KlaxonBlocks.HALLNOX_PRESSURE_PLATE.value(),
                        KlaxonBlocks.HALLNOX_BUTTON.value(),
                        KlaxonBlocks.HALLNOX_TRAPDOOR.value(),
                        KlaxonBlocks.HALLNOX_DOOR.value(),
                        KlaxonBlocks.HALLNOX_SIGN.value(),
                        KlaxonBlocks.HALLNOX_WALL_SIGN.value(),
                        KlaxonBlocks.HALLNOX_HANGING_SIGN.value(),
                        KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN.value(),
                        KlaxonBlocks.HALLNOX_FENCE.value(),
                        KlaxonBlocks.HALLNOX_FENCE_GATE.value()
                )
                .add(
                        KlaxonBlocks.RUBBER_BLOCK.value(),
                        KlaxonBlocks.RUBBER_SHEET_BLOCK.value(),
                        KlaxonBlocks.MOLTEN_RUBBER_BLOCK.value()
                );

        // hoe
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_HOE)
                .add(KlaxonBlocks.HALLNOX_POD.value())
                .add(KlaxonBlocks.HALLNOX_WART_BLOCK.value());
    }

    private void buildWrenchTags() {
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_PICKUP_ALLOWLIST)
                .forceAddTag(KlaxonBlockTags.WRENCH_INSTABREAKABLE)
                .forceAddTag(KlaxonConventionalBlockTags.GRATES)
                .add(Blocks.REDSTONE_TORCH)
                .add(Blocks.REPEATER)
                .add(Blocks.COMPARATOR)
                .add(Blocks.REDSTONE_WIRE);
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_PICKUP_DENYLIST)
                .forceAddTag(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED)
                .add(Blocks.PISTON_HEAD);
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_INTERACTION_GENERAL_DENYLIST)
                .forceAddTag(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED)
                .add(Blocks.LEVER)
                .add(Blocks.PISTON_HEAD);

        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.FACING.value().getAllowlistTag())
                .forceAddTag(BlockTags.SHULKER_BOXES)
                .add(KlaxonBlocks.PRECISION_DISPENSER.value())
                .add(KlaxonBlocks.STEEL_WORKBENCH.value())
                .add(Blocks.PISTON)
                .add(Blocks.STICKY_PISTON)
                .add(Blocks.OBSERVER);
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.FACING.value().getDenylistTag());
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.HORIZONTAL_FACING.value().getAllowlistTag())
                .add(KlaxonBlocks.STEEL_BLAST_PROCESSOR.value())
                .add(Blocks.BLAST_FURNACE)
                .add(Blocks.REPEATER)
                .add(Blocks.COMPARATOR);
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.HORIZONTAL_FACING.value().getDenylistTag());
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.HOPPER_FACING.value().getAllowlistTag())
                .add(Blocks.HOPPER);
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.HOPPER_FACING.value().getDenylistTag());
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.AXIS.value().getAllowlistTag());
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.AXIS.value().getDenylistTag());
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.HORIZONTAL_AXIS.value().getAllowlistTag())
                .add(KlaxonBlocks.NETHER_REACTOR_CORE.value())
                .add(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE.value());
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.HORIZONTAL_FACING.value().getDenylistTag());
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.ORIENTATION.value().getAllowlistTag())
                .add(Blocks.CRAFTER);
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.ORIENTATION.value().getDenylistTag());
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.CURVING_RAIL_SHAPE.value().getAllowlistTag())
                .forceAddTag(BlockTags.RAILS);
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.CURVING_RAIL_SHAPE.value().getDenylistTag());
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.STRAIGHT_RAIL_SHAPE.value().getAllowlistTag())
                .forceAddTag(BlockTags.RAILS);
        getOrCreateTagBuilder(KlaxonBlockStateWrenchBehaviors.STRAIGHT_RAIL_SHAPE.value().getDenylistTag());
    }

    private void buildBehaviorTags() {
        getOrCreateTagBuilder(BlockTags.BEACON_BASE_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK.value());
        getOrCreateTagBuilder(KlaxonBlockTags.COLD_BLOCKS)
                .forceAddTag(BlockTags.ICE)
                .forceAddTag(BlockTags.SNOW)
                .add(Blocks.POWDER_SNOW);

        getOrCreateTagBuilder(BlockTags.OCCLUDES_VIBRATION_SIGNALS)
                .add(KlaxonBlocks.RUBBER_BLOCK.value())
                .add(KlaxonBlocks.RUBBER_SHEET_BLOCK.value());
        getOrCreateTagBuilder(BlockTags.DAMPENS_VIBRATIONS)
                .add(KlaxonBlocks.RUBBER_BLOCK.value())
                .add(KlaxonBlocks.RUBBER_SHEET_BLOCK.value());

        // hallnox pod
        getOrCreateTagBuilder(KlaxonBlockTags.HALLNOX_POD_NATURAL_GROWTH_INHIBITING)
                .add(
                        KlaxonBlocks.HALLNOX_WART_BLOCK.value(),
                        KlaxonBlocks.HALLNOX_STEM.value(),
                        KlaxonBlocks.HALLNOX_HYPHAE.value()
                );

        // grapple winch
        getOrCreateTagBuilder(KlaxonBlockTags.GRAPPLE_CLAW_BREAKABLE)
                .forceAddTag(KlaxonBlockTags.GRAPPLE_CLAW_VEINMINEABLE)
                .forceAddTag(KlaxonBlockTags.CLEAVER_INSTABREAKABLE)
                .add(Blocks.POINTED_DRIPSTONE);
        getOrCreateTagBuilder(KlaxonBlockTags.GRAPPLE_CLAW_VEINMINEABLE)
                .forceAddTag(KlaxonBlockTags.HAMMER_INSTABREAKABLE)
                .forceAddTag(KlaxonBlockTags.GLASS_VEINMINE_GROUP)
                .forceAddTag(KlaxonBlockTags.GLOWSTONE_VEINMINE_GROUP)
                .forceAddTag(KlaxonBlockTags.ICE_VEINMINE_GROUP)
                .forceAddTag(KlaxonBlockTags.CHORUS_VEINMINE_GROUP)
                .add(
                        Blocks.CHORUS_PLANT,
                        Blocks.CHORUS_FLOWER
                );

        getOrCreateTagBuilder(BlockTags.GUARDED_BY_PIGLINS)
                .add(KlaxonBlocks.GOLD_WIRE_SPOOL_BLOCK.value())
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK.value());

        getOrCreateTagBuilder(KlaxonBlockTags.STEEL_BLAST_PROCESSOR_EXHAUST_OVERWRITABLE_ALLOWLIST)
                .addOptionalTag(BlockTags.CANDLES)
                .addOptionalTag(BlockTags.LEAVES)
                .addOptionalTag(BlockTags.BANNERS)
                .add(Blocks.COBWEB)
        ;
        getOrCreateTagBuilder(KlaxonBlockTags.STEEL_BLAST_PROCESSOR_EXHAUST_OVERWRITABLE_DENYLIST)
                .add(Blocks.REPEATER)
                .add(Blocks.COMPARATOR);
        getOrCreateTagBuilder(KlaxonBlockTags.STEEL_BLAST_PROCESSOR_FIRE_HOLDERS)
                .addOptionalTag(BlockTags.CAMPFIRES);
    }

    private void buildVeinmineGroupTags() {
        getOrCreateTagBuilder(KlaxonBlockTags.GLASS_VEINMINE_GROUP)
                .forceAddTag(ConventionalBlockTags.GLASS_BLOCKS)
                .forceAddTag(ConventionalBlockTags.GLASS_PANES);
        getOrCreateTagBuilder(KlaxonBlockTags.CHORUS_VEINMINE_GROUP)
                .add(Blocks.CHORUS_FLOWER)
                .add(Blocks.CHORUS_PLANT);
        getOrCreateTagBuilder(KlaxonBlockTags.ICE_VEINMINE_GROUP)
                .add(Blocks.ICE)
                .add(Blocks.FROSTED_ICE);
        getOrCreateTagBuilder(KlaxonBlockTags.GLOWSTONE_VEINMINE_GROUP)
                .add(Blocks.GLOWSTONE);
    }

    private void buildNetherReactionTags() {
        getOrCreateTagBuilder(KlaxonBlockTags.NETHER_REACTION_IMMUNE)
                .forceAddTag(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED)
                .forceAddTag(ConventionalBlockTags.ORES_IN_GROUND_NETHERRACK)
                .forceAddTag(ConventionalBlockTags.ORE_BEARING_GROUND_NETHERRACK)
                .forceAddTag(ConventionalBlockTags.NETHERITE_SCRAP_ORES)
                .forceAddTag(ConventionalBlockTags.STORAGE_BLOCKS_NETHERITE)
                .forceAddTag(ConventionalBlockTags.NETHERRACKS)
                .forceAddTag(ConventionalBlockTags.NETHER_BRICK_FENCES)
                .forceAddTag(ConventionalBlockTags.STORAGE_BLOCKS_BONE_MEAL)
                .forceAddTag(KlaxonBlockTags.HALLNOX_STEMS)
                .forceAddTag(BlockTags.SOUL_SPEED_BLOCKS)
                .forceAddTag(BlockTags.SOUL_FIRE_BASE_BLOCKS)
                .forceAddTag(BlockTags.BASE_STONE_NETHER)
                .forceAddTag(BlockTags.CRIMSON_STEMS)
                .forceAddTag(BlockTags.WARPED_STEMS)
                .forceAddTag(BlockTags.WART_BLOCKS)
                .forceAddTag(BlockTags.NYLIUM)
                .forceAddTag(BlockTags.FIRE)
                .add(Blocks.SHROOMLIGHT)
                .add(
                        Blocks.BLACKSTONE_STAIRS,
                        Blocks.BLACKSTONE_SLAB,
                        Blocks.BLACKSTONE_WALL
                )
                .add(
                        Blocks.SOUL_TORCH,
                        Blocks.SOUL_WALL_TORCH,
                        Blocks.SOUL_LANTERN,
                        Blocks.SOUL_CAMPFIRE
                )
                .add(
                        Blocks.CRIMSON_ROOTS,
                        Blocks.CRIMSON_FUNGUS,
                        Blocks.POTTED_CRIMSON_FUNGUS,
                        Blocks.POTTED_CRIMSON_ROOTS,
                        Blocks.WEEPING_VINES,
                        Blocks.WEEPING_VINES_PLANT,
                        Blocks.CRIMSON_PLANKS,
                        Blocks.CRIMSON_STAIRS,
                        Blocks.CRIMSON_SLAB,
                        Blocks.CRIMSON_BUTTON,
                        Blocks.CRIMSON_PRESSURE_PLATE,
                        Blocks.CRIMSON_FENCE,
                        Blocks.CRIMSON_FENCE_GATE,
                        Blocks.CRIMSON_DOOR,
                        Blocks.CRIMSON_TRAPDOOR,
                        Blocks.CRIMSON_SIGN,
                        Blocks.CRIMSON_WALL_SIGN,
                        Blocks.CRIMSON_HANGING_SIGN,
                        Blocks.CRIMSON_WALL_HANGING_SIGN
                )
                .add(
                        Blocks.WARPED_ROOTS,
                        Blocks.WARPED_FUNGUS,
                        Blocks.POTTED_WARPED_FUNGUS,
                        Blocks.POTTED_WARPED_ROOTS,
                        Blocks.TWISTING_VINES,
                        Blocks.TWISTING_VINES_PLANT,
                        Blocks.WARPED_PLANKS,
                        Blocks.WARPED_STAIRS,
                        Blocks.WARPED_SLAB,
                        Blocks.WARPED_BUTTON,
                        Blocks.WARPED_PRESSURE_PLATE,
                        Blocks.WARPED_FENCE,
                        Blocks.WARPED_FENCE_GATE,
                        Blocks.WARPED_DOOR,
                        Blocks.WARPED_TRAPDOOR,
                        Blocks.WARPED_SIGN,
                        Blocks.WARPED_WALL_SIGN,
                        Blocks.WARPED_HANGING_SIGN,
                        Blocks.WARPED_WALL_HANGING_SIGN
                )
                .add(
                        KlaxonBlocks.HALLNOX_POD.value(),
                        KlaxonBlocks.POTTED_HALLNOX_POD.value(),
                        KlaxonBlocks.HALLNOX_PLANKS.value(),
                        KlaxonBlocks.HALLNOX_STAIRS.value(),
                        KlaxonBlocks.HALLNOX_SLAB.value(),
                        KlaxonBlocks.HALLNOX_BUTTON.value(),
                        KlaxonBlocks.HALLNOX_PRESSURE_PLATE.value(),
                        KlaxonBlocks.HALLNOX_FENCE.value(),
                        KlaxonBlocks.HALLNOX_FENCE_GATE.value(),
                        KlaxonBlocks.HALLNOX_DOOR.value(),
                        KlaxonBlocks.HALLNOX_TRAPDOOR.value(),
                        KlaxonBlocks.HALLNOX_SIGN.value(),
                        KlaxonBlocks.HALLNOX_WALL_SIGN.value(),
                        KlaxonBlocks.HALLNOX_HANGING_SIGN.value(),
                        KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN.value()
                );

        getOrCreateTagBuilder(KlaxonBlockTags.WITHER_SKELETON_SKULL_CONVERTIBLE)
                .add(Blocks.SKELETON_SKULL);
        getOrCreateTagBuilder(KlaxonBlockTags.WITHER_SKELETON_WALL_SKULL_CONVERTIBLE)
                .add(Blocks.SKELETON_WALL_SKULL);
        getOrCreateTagBuilder(KlaxonBlockTags.STEEL_CASING_CONVERTIBLE)
                .add(KlaxonBlocks.NETHER_REACTOR_CORE.value());
        getOrCreateTagBuilder(KlaxonBlockTags.AIR_CONVERTIBLE)
                .add(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE.value())
                .add(Blocks.SNOW);
        getOrCreateTagBuilder(KlaxonBlockTags.FIRE_CONVERTIBLE)
                .add(Blocks.MOSS_CARPET)
                .add(Blocks.GLOW_LICHEN)
                .add(Blocks.DEAD_BUSH);
        getOrCreateTagBuilder(KlaxonBlockTags.SHROOMLIGHT_CONVERTIBLE)
                .forceAddTag(ConventionalBlockTags.PUMPKINS)
                .add(Blocks.MELON)
                .add(Blocks.COCOA)
                .add(Blocks.BEE_NEST);
        getOrCreateTagBuilder(KlaxonBlockTags.BONE_BLOCK_CONVERTIBLE)
                .add(Blocks.CACTUS);

        // netherrack stuff
        getOrCreateTagBuilder(KlaxonBlockTags.NETHERRACK_CONVERTIBLE)
                .forceAddTag(BlockTags.TERRACOTTA)
                .add(Blocks.SNOW_BLOCK)
                .add(Blocks.POWDER_SNOW)
                .add(Blocks.FARMLAND)
                .add(Blocks.DIRT)
                .add(Blocks.DIRT_PATH)
                .add(Blocks.COARSE_DIRT)
                .add(Blocks.ROOTED_DIRT)
                .add(Blocks.MUDDY_MANGROVE_ROOTS);
        getOrCreateTagBuilder(KlaxonBlockTags.NETHER_GOLD_ORE_CONVERTIBLE)
                .forceAddTag(ConventionalBlockTags.COPPER_ORES)
                .forceAddTag(ConventionalBlockTags.IRON_ORES)
                .forceAddTag(ConventionalBlockTags.GOLD_ORES)
                .forceAddTag(ConventionalBlockTags.LAPIS_ORES);
        getOrCreateTagBuilder(KlaxonBlockTags.NETHER_QUARTZ_ORE_CONVERTIBLE)
                .forceAddTag(ConventionalBlockTags.COAL_ORES)
                .forceAddTag(ConventionalBlockTags.REDSTONE_ORES)
                .forceAddTag(ConventionalBlockTags.DIAMOND_ORES)
                .forceAddTag(ConventionalBlockTags.EMERALD_ORES);

        // soul stuff
        getOrCreateTagBuilder(KlaxonBlockTags.SOUL_SAND_CONVERTIBLE)
                .forceAddTag(ConventionalBlockTags.SANDS);
        getOrCreateTagBuilder(KlaxonBlockTags.SOUL_SOIL_CONVERTIBLE)
                .add(Blocks.CLAY)
                .add(Blocks.MUD)
                .add(Blocks.MUDDY_MANGROVE_ROOTS);
        getOrCreateTagBuilder(KlaxonBlockTags.SOUL_TORCH_CONVERTIBLE)
                .add(Blocks.TORCH)
                .add(Blocks.REDSTONE_TORCH);
        getOrCreateTagBuilder(KlaxonBlockTags.SOUL_WALL_TORCH_CONVERTIBLE)
                .add(Blocks.WALL_TORCH)
                .add(Blocks.REDSTONE_WALL_TORCH);
        getOrCreateTagBuilder(KlaxonBlockTags.SOUL_LANTERN_CONVERTIBLE)
                .add(Blocks.LANTERN);
        getOrCreateTagBuilder(KlaxonBlockTags.SOUL_CAMPFIRE_CONVERTIBLE)
                .add(Blocks.CAMPFIRE);

        // rough blackstone stuff
        getOrCreateTagBuilder(KlaxonBlockTags.BLACKSTONE_CONVERTIBLE)
                .forceAddTag(ConventionalBlockTags.STONES)
                .forceAddTag(ConventionalBlockTags.COBBLESTONES)
                .forceAddTag(ConventionalBlockTags.SANDSTONE_BLOCKS)
                .add(Blocks.INFESTED_DEEPSLATE)
                .add(Blocks.INFESTED_STONE)
                .add(Blocks.TUFF);
        getOrCreateTagBuilder(KlaxonBlockTags.BLACKSTONE_STAIRS_CONVERTIBLE)
                .add(Blocks.COBBLESTONE_STAIRS)
                .add(Blocks.COBBLED_DEEPSLATE_STAIRS)
                .add(Blocks.MOSSY_COBBLESTONE_STAIRS)
                .add(Blocks.SANDSTONE_STAIRS)
                .add(Blocks.RED_SANDSTONE_STAIRS)
                .add(Blocks.GRANITE_STAIRS)
                .add(Blocks.ANDESITE_STAIRS)
                .add(Blocks.DIORITE_STAIRS)
                .add(Blocks.TUFF_STAIRS);
        getOrCreateTagBuilder(KlaxonBlockTags.BLACKSTONE_SLAB_CONVERTIBLE)
                .add(Blocks.COBBLESTONE_SLAB)
                .add(Blocks.COBBLED_DEEPSLATE_SLAB)
                .add(Blocks.MOSSY_COBBLESTONE_SLAB)
                .add(Blocks.SANDSTONE_SLAB)
                .add(Blocks.RED_SANDSTONE_SLAB)
                .add(Blocks.GRANITE_SLAB)
                .add(Blocks.ANDESITE_SLAB)
                .add(Blocks.DIORITE_SLAB)
                .add(Blocks.TUFF_SLAB);
        getOrCreateTagBuilder(KlaxonBlockTags.BLACKSTONE_WALL_CONVERTIBLE)
                .add(Blocks.COBBLESTONE_WALL)
                .add(Blocks.COBBLED_DEEPSLATE_WALL)
                .add(Blocks.MOSSY_COBBLESTONE_WALL)
                .add(Blocks.SANDSTONE_WALL)
                .add(Blocks.RED_SANDSTONE_WALL)
                .add(Blocks.GRANITE_WALL)
                .add(Blocks.ANDESITE_WALL)
                .add(Blocks.DIORITE_WALL)
                .add(Blocks.TUFF_WALL);

        // basalt
        getOrCreateTagBuilder(KlaxonBlockTags.BASALT_CONVERTIBLE)
                .add(Blocks.POINTED_DRIPSTONE)
                .add(Blocks.DRIPSTONE_BLOCK);

        // crimson stuff
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_NYLIUM_CONVERTIBLE)
                .add(Blocks.GRASS_BLOCK)
                .add(Blocks.PODZOL)
                .add(Blocks.MOSS_BLOCK);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_FUNGUS_CONVERTIBLE)
                .forceAddTag(BlockTags.FLOWERS)
                .forceAddTag(BlockTags.SAPLINGS)
                .add(Blocks.SWEET_BERRY_BUSH);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_ROOTS_CONVERTIBLE)
                .add(Blocks.SHORT_GRASS)
                .add(Blocks.TALL_GRASS)
                .add(Blocks.FERN);
        getOrCreateTagBuilder(KlaxonBlockTags.NETHER_WART_BLOCK_CONVERTIBLE)
                .forceAddTag(BlockTags.LEAVES);
        getOrCreateTagBuilder(KlaxonBlockTags.WEEPING_VINE_CONVERTIBLE)
                .forceAddTag(BlockTags.CAVE_VINES)
                .forceAddTag(BlockTags.SAPLINGS)
                .add(Blocks.HANGING_ROOTS)
                .add(Blocks.VINE)
                .add(Blocks.GLOW_LICHEN);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_STEM_CONVERTIBLE)
                .forceAddTag(KlaxonConventionalBlockTags.NATURAL_WOODS);
        getOrCreateTagBuilder(KlaxonBlockTags.STRIPPED_CRIMSON_STEM_CONVERTIBLE)
                .forceAddTag(ConventionalBlockTags.STRIPPED_LOGS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_HYPHAE_CONVERTIBLE)
                .forceAddTag(KlaxonConventionalBlockTags.NATURAL_LOGS)
                .add(Blocks.MANGROVE_ROOTS);
        getOrCreateTagBuilder(KlaxonBlockTags.STRIPPED_CRIMSON_HYPHAE_CONVERTIBLE)
                .forceAddTag(ConventionalBlockTags.STRIPPED_WOODS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_PLANKS_CONVERTIBLE)
                .forceAddTag(BlockTags.PLANKS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_STAIRS_CONVERTIBLE)
                .forceAddTag(BlockTags.WOODEN_STAIRS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_SLAB_CONVERTIBLE)
                .forceAddTag(BlockTags.WOODEN_SLABS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_BUTTON_CONVERTIBLE)
                .forceAddTag(BlockTags.WOODEN_BUTTONS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_PRESSURE_PLATE_CONVERTIBLE)
                .forceAddTag(BlockTags.WOODEN_PRESSURE_PLATES);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_FENCE_CONVERTIBLE)
                .forceAddTag(BlockTags.WOODEN_FENCES);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_FENCE_GATE_CONVERTIBLE)
                .forceAddTag(BlockTags.FENCE_GATES);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_DOOR_CONVERTIBLE)
                .forceAddTag(BlockTags.WOODEN_DOORS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_TRAPDOOR_CONVERTIBLE)
                .forceAddTag(BlockTags.WOODEN_TRAPDOORS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_SIGN_CONVERTIBLE)
                .forceAddTag(BlockTags.STANDING_SIGNS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_WALL_SIGN_CONVERTIBLE)
                .forceAddTag(BlockTags.WALL_SIGNS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_HANGING_SIGN_CONVERTIBLE)
                .forceAddTag(BlockTags.CEILING_HANGING_SIGNS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_WALL_HANGING_SIGN_CONVERTIBLE)
                .forceAddTag(BlockTags.WALL_HANGING_SIGNS);

        // warped stuff
        getOrCreateTagBuilder(KlaxonBlockTags.WARPED_NYLIUM_CONVERTIBLE)
                .add(Blocks.MYCELIUM);
        getOrCreateTagBuilder(KlaxonBlockTags.WARPED_FUNGUS_CONVERTIBLE)
                .add(Blocks.RED_MUSHROOM)
                .add(Blocks.BROWN_MUSHROOM);
        getOrCreateTagBuilder(KlaxonBlockTags.TWISTING_VINES_CONVERTIBLE)
                .add(Blocks.BAMBOO_SAPLING)
                .add(Blocks.BAMBOO)
                .add(Blocks.SUGAR_CANE);
        getOrCreateTagBuilder(KlaxonBlockTags.WARPED_WART_BLOCK_CONVERTIBLE)
                .add(Blocks.RED_MUSHROOM_BLOCK)
                .add(Blocks.BROWN_MUSHROOM_BLOCK);
        getOrCreateTagBuilder(KlaxonBlockTags.WARPED_STEM_CONVERTIBLE)
                .add(Blocks.MUSHROOM_STEM);
    }

    private void buildManualItemApplicationTags() {
        getOrCreateTagBuilder(KlaxonBlockTags.NETHER_REACTOR_CORE_CONVERTIBLE)
                .add(KlaxonBlocks.STEEL_CASING.value());
        getOrCreateTagBuilder(KlaxonBlockTags.CRUDE_NETHER_REACTOR_CORE_CONVERTIBLE)
                .add(KlaxonBlocks.CRUDE_STEEL_CASING.value());
        getOrCreateTagBuilder(KlaxonBlockTags.STEEL_WORKBENCH_CONVERTIBLE)
                .add(KlaxonBlocks.STEEL_CASING.value());
    }

    private void buildCategoricalTags() {
        getOrCreateTagBuilder(ConventionalBlockTags.STORAGE_BLOCKS)
                .forceAddTag(KlaxonConventionalBlockTags.STEEL_STORAGE_BLOCKS)
                .forceAddTag(KlaxonConventionalBlockTags.CRUDE_STEEL_STORAGE_BLOCKS)
                .forceAddTag(KlaxonConventionalBlockTags.RUBBER_STORAGE_BLOCKS);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.STEEL_STORAGE_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK.value());
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.CRUDE_STEEL_STORAGE_BLOCKS)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK.value());
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.RUBBER_STORAGE_BLOCKS)
                .add(KlaxonBlocks.RUBBER_BLOCK.value())
                .add(KlaxonBlocks.RUBBER_SHEET_BLOCK.value());
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.VINES)
                .forceAddTag(BlockTags.CAVE_VINES)
                .add(Blocks.WEEPING_VINES_PLANT)
                .add(Blocks.WEEPING_VINES)
                .add(Blocks.TWISTING_VINES_PLANT)
                .add(Blocks.TWISTING_VINES)
                .add(Blocks.VINE);
        getOrCreateTagBuilder(ConventionalBlockTags.PLAYER_WORKSTATIONS_CRAFTING_TABLES)
                .add(KlaxonBlocks.STEEL_WORKBENCH.value());

        // wire spools
        getOrCreateTagBuilder(KlaxonBlockTags.WIRE_SPOOLS)
                .forceAddTag(KlaxonBlockTags.STEEL_WIRE_SPOOLS)
                .forceAddTag(KlaxonBlockTags.GOLD_WIRE_SPOOLS)
                .forceAddTag(KlaxonBlockTags.IRON_WIRE_SPOOLS)
                .forceAddTag(KlaxonBlockTags.COPPER_WIRE_SPOOLS);
        getOrCreateTagBuilder(KlaxonBlockTags.STEEL_WIRE_SPOOLS)
                .add(KlaxonBlocks.STEEL_WIRE_SPOOL_BLOCK.value());
        getOrCreateTagBuilder(KlaxonBlockTags.GOLD_WIRE_SPOOLS)
                .add(KlaxonBlocks.GOLD_WIRE_SPOOL_BLOCK.value());
        getOrCreateTagBuilder(KlaxonBlockTags.IRON_WIRE_SPOOLS)
                .add(KlaxonBlocks.IRON_WIRE_SPOOL_BLOCK.value());
        getOrCreateTagBuilder(KlaxonBlockTags.COPPER_WIRE_SPOOLS)
                .add(
                        KlaxonBlocks.COPPER_WIRE_SPOOL_BLOCK.value(),
                        KlaxonBlocks.EXPOSED_COPPER_WIRE_SPOOL_BLOCK.value(),
                        KlaxonBlocks.WEATHERED_COPPER_WIRE_SPOOL_BLOCK.value(),
                        KlaxonBlocks.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK.value(),
                        KlaxonBlocks.WAXED_COPPER_WIRE_SPOOL_BLOCK.value(),
                        KlaxonBlocks.WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK.value(),
                        KlaxonBlocks.WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK.value(),
                        KlaxonBlocks.WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK.value()
                );

        // pipe matrices
        getOrCreateTagBuilder(KlaxonBlockTags.PIPE_MATRICES)
                .forceAddTag(KlaxonBlockTags.PIPE_MATRIX_U_BENDS)
                .forceAddTag(KlaxonBlockTags.PIPE_MATRIX_SEGMENTS);

        getOrCreateTagBuilder(KlaxonBlockTags.PIPE_MATRIX_U_BENDS)
                .forceAddTag(KlaxonBlockTags.COPPER_PIPE_MATRIX_U_BENDS);
        getOrCreateTagBuilder(KlaxonBlockTags.PIPE_MATRIX_SEGMENTS)
                .forceAddTag(KlaxonBlockTags.COPPER_PIPE_MATRIX_SEGMENTS);

        getOrCreateTagBuilder(KlaxonBlockTags.COPPER_PIPE_MATRICES)
                .forceAddTag(KlaxonBlockTags.COPPER_PIPE_MATRIX_U_BENDS)
                .forceAddTag(KlaxonBlockTags.COPPER_PIPE_MATRIX_SEGMENTS);
        getOrCreateTagBuilder(KlaxonBlockTags.COPPER_PIPE_MATRIX_U_BENDS)
                .add(
                        KlaxonBlocks.COPPER_PIPE_MATRIX_U_BEND.value(),
                        KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_U_BEND.value(),
                        KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_U_BEND.value(),
                        KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_U_BEND.value()
                )
                .add(
                        KlaxonBlocks.WAXED_COPPER_PIPE_MATRIX_U_BEND.value(),
                        KlaxonBlocks.WAXED_EXPOSED_COPPER_PIPE_MATRIX_U_BEND.value(),
                        KlaxonBlocks.WAXED_WEATHERED_COPPER_PIPE_MATRIX_U_BEND.value(),
                        KlaxonBlocks.WAXED_OXIDIZED_COPPER_PIPE_MATRIX_U_BEND.value()
                )
                ;
        getOrCreateTagBuilder(KlaxonBlockTags.COPPER_PIPE_MATRIX_SEGMENTS)
                .add(
                        KlaxonBlocks.COPPER_PIPE_MATRIX_SEGMENT.value(),
                        KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_SEGMENT.value(),
                        KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_SEGMENT.value(),
                        KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT.value()
                )
                .add(
                        KlaxonBlocks.WAXED_COPPER_PIPE_MATRIX_SEGMENT.value(),
                        KlaxonBlocks.WAXED_EXPOSED_COPPER_PIPE_MATRIX_SEGMENT.value(),
                        KlaxonBlocks.WAXED_WEATHERED_COPPER_PIPE_MATRIX_SEGMENT.value(),
                        KlaxonBlocks.WAXED_OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT.value()
                );

        // funny
        getOrCreateTagBuilder(BlockTags.FLOWER_POTS)
                .add(KlaxonBlocks.POTTED_HALLNOX_POD.value());

        // fences + doors + trapdoors + pressure plates + buttons + etc...
        getOrCreateTagBuilder(BlockTags.WOODEN_FENCES)
                .add(KlaxonBlocks.HALLNOX_FENCE.value());
        getOrCreateTagBuilder(ConventionalBlockTags.WOODEN_FENCE_GATES)
                .add(KlaxonBlocks.HALLNOX_FENCE_GATE.value());
        getOrCreateTagBuilder(BlockTags.DOORS)
                .forceAddTag(KlaxonConventionalBlockTags.METAL_DOORS);
        getOrCreateTagBuilder(BlockTags.TRAPDOORS)
                .forceAddTag(KlaxonConventionalBlockTags.METAL_TRAPDOORS);
        getOrCreateTagBuilder(BlockTags.WOODEN_BUTTONS)
                .add(KlaxonBlocks.HALLNOX_BUTTON.value());
        getOrCreateTagBuilder(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(KlaxonBlocks.HALLNOX_PRESSURE_PLATE.value());
        getOrCreateTagBuilder(BlockTags.WOODEN_DOORS)
                .add(KlaxonBlocks.HALLNOX_DOOR.value());
        getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS)
                .add(KlaxonBlocks.HALLNOX_TRAPDOOR.value());
        getOrCreateTagBuilder(BlockTags.STANDING_SIGNS)
                .add(KlaxonBlocks.HALLNOX_SIGN.value());
        getOrCreateTagBuilder(BlockTags.WALL_SIGNS)
                .add(KlaxonBlocks.HALLNOX_WALL_SIGN.value());
        getOrCreateTagBuilder(BlockTags.CEILING_HANGING_SIGNS)
                .add(KlaxonBlocks.HALLNOX_HANGING_SIGN.value());
        getOrCreateTagBuilder(BlockTags.WALL_HANGING_SIGNS)
                .add(KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN.value());

        // planks + stairs + slabs + etc...
        getOrCreateTagBuilder(BlockTags.PLANKS)
                .add(KlaxonBlocks.HALLNOX_PLANKS.value());
        getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS)
                .add(KlaxonBlocks.HALLNOX_STAIRS.value());
        getOrCreateTagBuilder(BlockTags.WOODEN_SLABS)
                .add(KlaxonBlocks.HALLNOX_SLAB.value());

        // logs + wood + leaves...
        getOrCreateTagBuilder(ConventionalBlockTags.STRIPPED_LOGS)
                .add(KlaxonBlocks.STRIPPED_HALLNOX_STEM.value());
        getOrCreateTagBuilder(ConventionalBlockTags.STRIPPED_WOODS)
                .add(KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE.value());
        getOrCreateTagBuilder(BlockTags.LOGS)
                .forceAddTag(KlaxonBlockTags.HALLNOX_STEMS);
        getOrCreateTagBuilder(KlaxonBlockTags.HALLNOX_STEMS)
                .add(KlaxonBlocks.HALLNOX_STEM.value())
                .add(KlaxonBlocks.STRIPPED_HALLNOX_STEM.value())
                .add(KlaxonBlocks.HALLNOX_HYPHAE.value())
                .add(KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE.value());
        getOrCreateTagBuilder(BlockTags.WART_BLOCKS)
                .add(KlaxonBlocks.HALLNOX_WART_BLOCK.value());
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.NATURAL_LOGS)
                .add(
                        Blocks.OAK_WOOD,
                        Blocks.SPRUCE_WOOD,
                        Blocks.BIRCH_WOOD,
                        Blocks.JUNGLE_WOOD,
                        Blocks.ACACIA_WOOD,
                        Blocks.DARK_OAK_WOOD,
                        Blocks.MANGROVE_WOOD,
                        Blocks.CHERRY_WOOD
                )
                .add(
                        Blocks.CRIMSON_HYPHAE,
                        Blocks.WARPED_HYPHAE,
                        KlaxonBlocks.HALLNOX_HYPHAE.value()
                );
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.NATURAL_WOODS)
                .add(
                        Blocks.OAK_LOG,
                        Blocks.SPRUCE_LOG,
                        Blocks.BIRCH_LOG,
                        Blocks.JUNGLE_LOG,
                        Blocks.ACACIA_LOG,
                        Blocks.DARK_OAK_LOG,
                        Blocks.MANGROVE_LOG,
                        Blocks.CHERRY_LOG
                )
                .add(
                        Blocks.CRIMSON_STEM,
                        Blocks.WARPED_STEM,
                        KlaxonBlocks.HALLNOX_STEM.value()
                );

        getOrCreateTagBuilder(KlaxonConventionalBlockTags.METAL_DOORS)
                .add(KlaxonBlocks.STEEL_DOOR.value())
                .add(KlaxonBlocks.CRUDE_STEEL_DOOR.value())
                .add(Blocks.COPPER_DOOR)
                .add(Blocks.EXPOSED_COPPER_DOOR)
                .add(Blocks.WEATHERED_COPPER_DOOR)
                .add(Blocks.OXIDIZED_COPPER_DOOR)
                .add(Blocks.WAXED_COPPER_DOOR)
                .add(Blocks.WAXED_EXPOSED_COPPER_DOOR)
                .add(Blocks.WAXED_WEATHERED_COPPER_DOOR)
                .add(Blocks.WAXED_OXIDIZED_COPPER_DOOR);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.METAL_TRAPDOORS)
                .add(KlaxonBlocks.STEEL_TRAPDOOR.value())
                .add(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR.value())
                .add(Blocks.COPPER_TRAPDOOR)
                .add(Blocks.EXPOSED_COPPER_TRAPDOOR)
                .add(Blocks.WEATHERED_COPPER_TRAPDOOR)
                .add(Blocks.OXIDIZED_COPPER_TRAPDOOR)
                .add(Blocks.WAXED_COPPER_TRAPDOOR)
                .add(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR)
                .add(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR)
                .add(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);

        getOrCreateTagBuilder(KlaxonConventionalBlockTags.SCAFFOLDINGS)
                .add(Blocks.SCAFFOLDING);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.GRATES)
                .add(Blocks.COPPER_GRATE)
                .add(Blocks.EXPOSED_COPPER_GRATE)
                .add(Blocks.WEATHERED_COPPER_GRATE)
                .add(Blocks.OXIDIZED_COPPER_GRATE)
                .add(Blocks.WAXED_COPPER_GRATE)
                .add(Blocks.WAXED_EXPOSED_COPPER_GRATE)
                .add(Blocks.WAXED_WEATHERED_COPPER_GRATE)
                .add(Blocks.WAXED_OXIDIZED_COPPER_GRATE);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.LEVERS)
                .add(Blocks.LEVER);

        getOrCreateTagBuilder(KlaxonBlockTags.MACHINES)
                .forceAddTag(KlaxonBlockTags.BLAST_PROCESSORS)
                .forceAddTag(KlaxonBlockTags.NETHER_REACTOR_CORES);
        getOrCreateTagBuilder(KlaxonBlockTags.NETHER_REACTOR_CORES)
                .add(KlaxonBlocks.NETHER_REACTOR_CORE.value())
                .add(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE.value());
        getOrCreateTagBuilder(KlaxonBlockTags.BLAST_PROCESSORS)
                .add(KlaxonBlocks.STEEL_BLAST_PROCESSOR.value())
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.value());

        // plating blocks
        getOrCreateTagBuilder(KlaxonBlockTags.PLATING_BLOCKS)
                .forceAddTag(KlaxonBlockTags.STEEL_PLATING_BLOCKS)
                .forceAddTag(KlaxonBlockTags.CRUDE_STEEL_PLATING_BLOCKS)
                .forceAddTag(KlaxonBlockTags.GOLD_PLATING_BLOCKS)
                .forceAddTag(KlaxonBlockTags.COPPER_PLATING_BLOCKS)
                .forceAddTag(KlaxonBlockTags.IRON_PLATING_BLOCKS);
        getOrCreateTagBuilder(KlaxonBlockTags.STEEL_PLATING_BLOCKS)
                .add(KlaxonBlocks.STEEL_PLATING_BLOCK.value());
        getOrCreateTagBuilder(KlaxonBlockTags.CRUDE_STEEL_PLATING_BLOCKS)
                .add(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK.value());
        getOrCreateTagBuilder(KlaxonBlockTags.GOLD_PLATING_BLOCKS)
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK.value());
        getOrCreateTagBuilder(KlaxonBlockTags.IRON_PLATING_BLOCKS)
                .add(KlaxonBlocks.IRON_PLATING_BLOCK.value());
        getOrCreateTagBuilder(KlaxonBlockTags.COPPER_PLATING_BLOCKS)
                .add(
                        KlaxonBlocks.COPPER_PLATING_BLOCK.value(),
                        KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK.value(),
                        KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK.value(),
                        KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK.value()
                )
                .add(
                        KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK.value(),
                        KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK.value(),
                        KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK.value(),
                        KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK.value()
                );
    }

    private void buildToolRequirementTags() {
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .forceAddTag(KlaxonBlockTags.STEEL_WIRE_SPOOLS)
                .forceAddTag(KlaxonBlockTags.STEEL_PLATING_BLOCKS)
                .forceAddTag(KlaxonBlockTags.GOLD_WIRE_SPOOLS)
                .forceAddTag(KlaxonBlockTags.GOLD_PLATING_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK.value())
                .add(KlaxonBlocks.STEEL_CASING.value())
                .add(KlaxonBlocks.STEEL_DOOR.value())
                .add(KlaxonBlocks.STEEL_TRAPDOOR.value())
                .add(KlaxonBlocks.STEEL_WORKBENCH.value())
                .add(KlaxonBlocks.HEAVY_GATED_PRESSURE_PLATE.value())
                .add(KlaxonBlocks.STEEL_BLAST_PROCESSOR.value())
                .add(KlaxonBlocks.NETHER_REACTOR_CORE.value());
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .forceAddTag(KlaxonBlockTags.COPPER_PIPE_MATRIX_SEGMENTS)
                .forceAddTag(KlaxonBlockTags.COPPER_PIPE_MATRIX_U_BENDS)
                .forceAddTag(KlaxonBlockTags.COPPER_WIRE_SPOOLS)
                .forceAddTag(KlaxonBlockTags.COPPER_PLATING_BLOCKS)
                .forceAddTag(KlaxonBlockTags.IRON_WIRE_SPOOLS)
                .forceAddTag(KlaxonBlockTags.IRON_PLATING_BLOCKS)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK.value())
                .add(KlaxonBlocks.CRUDE_STEEL_CASING.value())
                .add(KlaxonBlocks.CRUDE_STEEL_DOOR.value())
                .add(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR.value())
                .add(KlaxonBlocks.FAULTY_HEAVY_GATED_PRESSURE_PLATE.value())
                .add(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE.value());

        // steel tools mirror diamond tools
        getOrCreateTagBuilder(KlaxonBlockTags.NEEDS_STEEL_TOOL)
                .forceAddTag(BlockTags.NEEDS_DIAMOND_TOOL);
        getOrCreateTagBuilder(KlaxonBlockTags.INCORRECT_FOR_STEEL_TOOL)
                .forceAddTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL);

    }

    private void buildCompatTags() {
        getOrCreateTagBuilder(KlaxonCompatBlockTags.IGNITE_FLINT_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK.value())
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK.value())
                .add(KlaxonBlocks.STEEL_PLATING_BLOCK.value())
                .add(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK.value())
                .add(KlaxonBlocks.STEEL_CASING.value())
                .add(KlaxonBlocks.CRUDE_STEEL_CASING.value());
    }

    @Override
    protected FabricTagProvider<Block>.FabricTagBuilder getOrCreateTagBuilder(TagKey<Block> tag) {
        return super.getOrCreateTagBuilder(tag);
    }
}
