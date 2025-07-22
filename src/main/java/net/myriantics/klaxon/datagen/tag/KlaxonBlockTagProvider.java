package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.tag.compat.KlaxonCompatBlockTags;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public KlaxonBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        buildMiningTags();
        buildBehaviorTags();
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
                .add(Blocks.BEACON);
        getOrCreateTagBuilder(KlaxonBlockTags.HAMMER_INSTABREAKABLE)
                .addOptionalTag(KlaxonCompatBlockTags.BRICK_BREAKABLE)
                .forceAddTag(ConventionalBlockTags.GLASS_BLOCKS)
                .forceAddTag(ConventionalBlockTags.GLASS_PANES)
                .forceAddTag(ConventionalBlockTags.BUDS)
                .forceAddTag(ConventionalBlockTags.CLUSTERS)
                .forceAddTag(ConventionalBlockTags.SKULLS)
                .forceAddTag(BlockTags.ICE)
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
                .forceAddTag(BlockTags.CEILING_HANGING_SIGNS)
                .forceAddTag(BlockTags.WOOL_CARPETS)
                .forceAddTag(BlockTags.WOOL)
                .forceAddTag(BlockTags.LEAVES)
                .add(Blocks.IRON_BARS);

        // cleaver
        getOrCreateTagBuilder(KlaxonBlockTags.CLEAVER_MINEABLE)
                .forceAddTag(KlaxonBlockTags.CLEAVER_INSTABREAKABLE)
                .forceAddTag(BlockTags.AXE_MINEABLE)
                .add(Blocks.COBWEB);
        getOrCreateTagBuilder(KlaxonBlockTags.CLEAVER_INSTABREAKABLE)
                .forceAddTag(KlaxonConventionalBlockTags.RUBBER)
                .forceAddTag(KlaxonConventionalBlockTags.SCULK)
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
                .add(KlaxonBlocks.HALLNOX_POD)
                .add(Blocks.COCOA)
                .add(Blocks.OCHRE_FROGLIGHT)
                .add(Blocks.PEARLESCENT_FROGLIGHT)
                .add(Blocks.VERDANT_FROGLIGHT)
                .add(Blocks.MOSS_BLOCK)
                .add(Blocks.MOSS_CARPET)
                .add(Blocks.AZALEA)
                .add(Blocks.FLOWERING_AZALEA)
                .add(Blocks.SHROOMLIGHT)
                .add(Blocks.MELON)
                .add(Blocks.BAMBOO)
                .add(Blocks.HONEYCOMB_BLOCK)
                .add(Blocks.GLOW_LICHEN)
                .add(Blocks.CAKE);

        // wrench
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_MINEABLE)
                .forceAddTag(KlaxonBlockTags.WRENCH_INSTABREAKABLE)
                .forceAddTag(BlockTags.TRAPDOORS)
                .forceAddTag(BlockTags.DOORS);
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_INSTABREAKABLE)
                .forceAddTag(BlockTags.SHULKER_BOXES)
                .forceAddTag(BlockTags.RAILS)
                .forceAddTag(KlaxonConventionalBlockTags.METAL_DOORS)
                .forceAddTag(KlaxonConventionalBlockTags.METAL_TRAPDOORS)
                .forceAddTag(KlaxonConventionalBlockTags.SCAFFOLDINGS)
                .forceAddTag(KlaxonConventionalBlockTags.LEVERS)
                .addOptionalTag(KlaxonCompatBlockTags.WRENCH_PICKUP)
                .add(KlaxonBlocks.STEEL_CASING)
                .add(KlaxonBlocks.CRUDE_STEEL_CASING)
                .add(KlaxonBlocks.NETHER_REACTOR_CORE)
                .add(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE)
                .add(Blocks.DAYLIGHT_DETECTOR)
                .add(Blocks.HOPPER)
                .add(Blocks.COPPER_BULB)
                .add(Blocks.EXPOSED_COPPER_BULB)
                .add(Blocks.WEATHERED_COPPER_BULB)
                .add(Blocks.OXIDIZED_COPPER_BULB)
                .add(Blocks.WAXED_COPPER_BULB)
                .add(Blocks.WAXED_EXPOSED_COPPER_BULB)
                .add(Blocks.WAXED_WEATHERED_COPPER_BULB)
                .add(Blocks.WAXED_OXIDIZED_COPPER_BULB)
                .add(Blocks.REDSTONE_LAMP)
                .add(Blocks.BLAST_FURNACE)
                .add(Blocks.STICKY_PISTON)
                .add(Blocks.PISTON_HEAD)
                .add(Blocks.PISTON)
                .add(Blocks.OBSERVER)
                .add(Blocks.CRAFTER);

        // pickaxe
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .forceAddTag(KlaxonConventionalBlockTags.STEEL)
                .forceAddTag(KlaxonConventionalBlockTags.CRUDE_STEEL)
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)
                .add(KlaxonBlocks.NETHER_REACTOR_CORE)
                .add(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE)
                .add(KlaxonBlocks.IRON_PLATING_BLOCK)
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK)
                .add(KlaxonBlocks.COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);

        // axe
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .forceAddTag(KlaxonConventionalBlockTags.RUBBER)
                .add(KlaxonBlocks.HALLNOX_POD)
                .add(KlaxonBlocks.HALLNOX_STEM)
                .add(KlaxonBlocks.STRIPPED_HALLNOX_STEM)
                .add(KlaxonBlocks.HALLNOX_HYPHAE)
                .add(KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE)
                .add(KlaxonBlocks.HALLNOX_PLANKS)
                .add(KlaxonBlocks.HALLNOX_STAIRS)
                .add(KlaxonBlocks.HALLNOX_SLAB)
                .add(KlaxonBlocks.HALLNOX_PRESSURE_PLATE)
                .add(KlaxonBlocks.HALLNOX_BUTTON)
                .add(KlaxonBlocks.HALLNOX_TRAPDOOR)
                .add(KlaxonBlocks.HALLNOX_DOOR)
                .add(KlaxonBlocks.HALLNOX_SIGN)
                .add(KlaxonBlocks.HALLNOX_WALL_SIGN)
                .add(KlaxonBlocks.HALLNOX_HANGING_SIGN)
                .add(KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN)
                .add(KlaxonBlocks.HALLNOX_FENCE)
                .add(KlaxonBlocks.HALLNOX_FENCE_GATE);

        // hoe
        getOrCreateTagBuilder(BlockTags.HOE_MINEABLE)
                .add(KlaxonBlocks.HALLNOX_POD)
                .add(KlaxonBlocks.HALLNOX_WART_BLOCK);
    }

    private void buildBehaviorTags() {
        getOrCreateTagBuilder(BlockTags.BEACON_BASE_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK);
        getOrCreateTagBuilder(BlockTags.GUARDED_BY_PIGLINS)
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK);
        getOrCreateTagBuilder(KlaxonBlockTags.COLD_BLOCKS)
                .forceAddTag(BlockTags.ICE)
                .forceAddTag(BlockTags.SNOW)
                .add(Blocks.POWDER_SNOW);

        // wrench
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
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_ROTATION_ALLOWLIST)
                .forceAddTag(KlaxonBlockTags.WRENCH_INSTABREAKABLE);
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_ROTATION_DENYLIST)
                .forceAddTag(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED)
                .forceAddTag(BlockTags.TRAPDOORS)
                .forceAddTag(BlockTags.DOORS)
                .add(Blocks.LEVER)
                .add(Blocks.PISTON_HEAD);
    }

    private void buildNetherReactionTags() {
        getOrCreateTagBuilder(KlaxonBlockTags.STEEL_CASING_CONVERTIBLE)
                .add(KlaxonBlocks.NETHER_REACTOR_CORE);
        getOrCreateTagBuilder(KlaxonBlockTags.AIR_CONVERTIBLE)
                .add(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE);
        getOrCreateTagBuilder(KlaxonBlockTags.FIRE_CONVERTIBLE)
                .add(Blocks.MOSS_CARPET)
                .add(Blocks.GLOW_LICHEN)
                .add(Blocks.DEAD_BUSH);
        getOrCreateTagBuilder(KlaxonBlockTags.SHROOMLIGHT_CONVERTIBLE)
                .forceAddTag(ConventionalBlockTags.PUMPKINS)
                .add(Blocks.MELON)
                .add(Blocks.COCOA);
        getOrCreateTagBuilder(KlaxonBlockTags.BONE_BLOCK_CONVERTIBLE)
                .add(Blocks.CACTUS);

        // netherrack stuff
        getOrCreateTagBuilder(KlaxonBlockTags.NETHERRACK_CONVERTIBLE)
                .forceAddTag(BlockTags.TERRACOTTA)
                .add(Blocks.FARMLAND)
                .add(Blocks.DIRT)
                .add(Blocks.DIRT_PATH)
                .add(Blocks.COARSE_DIRT)
                .add(Blocks.ROOTED_DIRT);
        getOrCreateTagBuilder(KlaxonBlockTags.NETHER_GOLD_ORE_CONVERTIBLE)
                .forceAddTag(BlockTags.COPPER_ORES)
                .forceAddTag(BlockTags.IRON_ORES)
                .forceAddTag(BlockTags.GOLD_ORES)
                .forceAddTag(BlockTags.LAPIS_ORES);
        getOrCreateTagBuilder(KlaxonBlockTags.NETHER_QUARTZ_ORE_CONVERTIBLE)
                .forceAddTag(BlockTags.COAL_ORES)
                .forceAddTag(BlockTags.REDSTONE_ORES)
                .forceAddTag(BlockTags.DIAMOND_ORES)
                .forceAddTag(BlockTags.EMERALD_ORES);

        // soul stuff
        getOrCreateTagBuilder(KlaxonBlockTags.SOUL_SAND_CONVERTIBLE)
                .add(Blocks.SAND)
                .add(Blocks.RED_SAND)
                .add(Blocks.SUSPICIOUS_SAND);
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
                .add(Blocks.OAK_LOG)
                .add(Blocks.SPRUCE_LOG)
                .add(Blocks.BIRCH_LOG)
                .add(Blocks.JUNGLE_LOG)
                .add(Blocks.ACACIA_LOG)
                .add(Blocks.DARK_OAK_LOG)
                .add(Blocks.MANGROVE_LOG)
                .add(Blocks.CHERRY_LOG)
                .add(Blocks.MANGROVE_ROOTS);
        getOrCreateTagBuilder(KlaxonBlockTags.STRIPPED_CRIMSON_STEM_CONVERTIBLE)
                .add(Blocks.STRIPPED_OAK_LOG)
                .add(Blocks.STRIPPED_SPRUCE_LOG)
                .add(Blocks.STRIPPED_BIRCH_LOG)
                .add(Blocks.STRIPPED_JUNGLE_LOG)
                .add(Blocks.STRIPPED_ACACIA_LOG)
                .add(Blocks.STRIPPED_DARK_OAK_LOG)
                .add(Blocks.STRIPPED_MANGROVE_LOG)
                .add(Blocks.STRIPPED_CHERRY_LOG);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_HYPHAE_CONVERTIBLE)
                .add(Blocks.OAK_WOOD)
                .add(Blocks.SPRUCE_WOOD)
                .add(Blocks.BIRCH_WOOD)
                .add(Blocks.JUNGLE_WOOD)
                .add(Blocks.ACACIA_WOOD)
                .add(Blocks.DARK_OAK_WOOD)
                .add(Blocks.MANGROVE_WOOD)
                .add(Blocks.CHERRY_WOOD);
        getOrCreateTagBuilder(KlaxonBlockTags.STRIPPED_CRIMSON_HYPHAE_CONVERTIBLE)
                .add(Blocks.STRIPPED_OAK_WOOD)
                .add(Blocks.STRIPPED_SPRUCE_WOOD)
                .add(Blocks.STRIPPED_BIRCH_WOOD)
                .add(Blocks.STRIPPED_JUNGLE_WOOD)
                .add(Blocks.STRIPPED_ACACIA_WOOD)
                .add(Blocks.STRIPPED_DARK_OAK_WOOD)
                .add(Blocks.STRIPPED_MANGROVE_WOOD)
                .add(Blocks.STRIPPED_CHERRY_WOOD);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_PLANKS_CONVERTIBLE)
                .add(Blocks.OAK_PLANKS)
                .add(Blocks.SPRUCE_PLANKS)
                .add(Blocks.BIRCH_PLANKS)
                .add(Blocks.JUNGLE_PLANKS)
                .add(Blocks.ACACIA_PLANKS)
                .add(Blocks.DARK_OAK_PLANKS)
                .add(Blocks.MANGROVE_PLANKS)
                .add(Blocks.CHERRY_PLANKS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_STAIRS_CONVERTIBLE)
                .add(Blocks.OAK_STAIRS)
                .add(Blocks.SPRUCE_STAIRS)
                .add(Blocks.BIRCH_STAIRS)
                .add(Blocks.JUNGLE_STAIRS)
                .add(Blocks.ACACIA_STAIRS)
                .add(Blocks.DARK_OAK_STAIRS)
                .add(Blocks.MANGROVE_STAIRS)
                .add(Blocks.CHERRY_STAIRS);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_SLAB_CONVERTIBLE)
                .add(Blocks.OAK_SLAB)
                .add(Blocks.SPRUCE_SLAB)
                .add(Blocks.BIRCH_SLAB)
                .add(Blocks.JUNGLE_SLAB)
                .add(Blocks.ACACIA_SLAB)
                .add(Blocks.DARK_OAK_SLAB)
                .add(Blocks.MANGROVE_SLAB)
                .add(Blocks.CHERRY_SLAB);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_BUTTON_CONVERTIBLE)
                .add(Blocks.OAK_BUTTON)
                .add(Blocks.SPRUCE_BUTTON)
                .add(Blocks.BIRCH_BUTTON)
                .add(Blocks.JUNGLE_BUTTON)
                .add(Blocks.ACACIA_BUTTON)
                .add(Blocks.DARK_OAK_BUTTON)
                .add(Blocks.MANGROVE_BUTTON)
                .add(Blocks.CHERRY_BUTTON);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_PRESSURE_PLATE_CONVERTIBLE)
                .add(Blocks.OAK_PRESSURE_PLATE)
                .add(Blocks.SPRUCE_PRESSURE_PLATE)
                .add(Blocks.BIRCH_PRESSURE_PLATE)
                .add(Blocks.JUNGLE_PRESSURE_PLATE)
                .add(Blocks.ACACIA_PRESSURE_PLATE)
                .add(Blocks.DARK_OAK_PRESSURE_PLATE)
                .add(Blocks.MANGROVE_PRESSURE_PLATE)
                .add(Blocks.CHERRY_PRESSURE_PLATE);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_FENCE_CONVERTIBLE)
                .add(Blocks.OAK_FENCE)
                .add(Blocks.SPRUCE_FENCE)
                .add(Blocks.BIRCH_FENCE)
                .add(Blocks.JUNGLE_FENCE)
                .add(Blocks.ACACIA_FENCE)
                .add(Blocks.DARK_OAK_FENCE)
                .add(Blocks.MANGROVE_FENCE)
                .add(Blocks.CHERRY_FENCE);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_FENCE_GATE_CONVERTIBLE)
                .add(Blocks.OAK_FENCE_GATE)
                .add(Blocks.SPRUCE_FENCE_GATE)
                .add(Blocks.BIRCH_FENCE_GATE)
                .add(Blocks.JUNGLE_FENCE_GATE)
                .add(Blocks.ACACIA_FENCE_GATE)
                .add(Blocks.DARK_OAK_FENCE_GATE)
                .add(Blocks.MANGROVE_FENCE_GATE)
                .add(Blocks.CHERRY_FENCE_GATE);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_DOOR_CONVERTIBLE)
                .add(Blocks.OAK_DOOR)
                .add(Blocks.SPRUCE_DOOR)
                .add(Blocks.BIRCH_DOOR)
                .add(Blocks.JUNGLE_DOOR)
                .add(Blocks.ACACIA_DOOR)
                .add(Blocks.DARK_OAK_DOOR)
                .add(Blocks.MANGROVE_DOOR)
                .add(Blocks.CHERRY_DOOR);
        getOrCreateTagBuilder(KlaxonBlockTags.CRIMSON_TRAPDOOR_CONVERTIBLE)
                .add(Blocks.OAK_TRAPDOOR)
                .add(Blocks.SPRUCE_TRAPDOOR)
                .add(Blocks.BIRCH_TRAPDOOR)
                .add(Blocks.JUNGLE_TRAPDOOR)
                .add(Blocks.ACACIA_TRAPDOOR)
                .add(Blocks.DARK_OAK_TRAPDOOR)
                .add(Blocks.MANGROVE_TRAPDOOR)
                .add(Blocks.CHERRY_TRAPDOOR);

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
                .add(KlaxonBlocks.STEEL_CASING);
        getOrCreateTagBuilder(KlaxonBlockTags.CRUDE_NETHER_REACTOR_CORE_CONVERTIBLE)
                .add(KlaxonBlocks.CRUDE_STEEL_CASING);
    }

    private void buildCategoricalTags() {
        getOrCreateTagBuilder(ConventionalBlockTags.STORAGE_BLOCKS)
                .forceAddTag(KlaxonConventionalBlockTags.STEEL_STORAGE_BLOCKS)
                .forceAddTag(KlaxonConventionalBlockTags.CRUDE_STEEL_STORAGE_BLOCKS)
                .forceAddTag(KlaxonConventionalBlockTags.RUBBER_STORAGE_BLOCKS);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.STEEL_STORAGE_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.CRUDE_STEEL_STORAGE_BLOCKS)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.RUBBER_STORAGE_BLOCKS)
                .add(KlaxonBlocks.RUBBER_BLOCK)
                .add(KlaxonBlocks.RUBBER_SHEET_BLOCK);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.SCULK)
                .add(Blocks.SCULK_VEIN)
                .add(Blocks.SCULK)
                .add(Blocks.SCULK_CATALYST)
                .add(Blocks.SCULK_SENSOR)
                .add(Blocks.SCULK_SHRIEKER)
                .add(Blocks.CALIBRATED_SCULK_SENSOR);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.VINES)
                .forceAddTag(BlockTags.CAVE_VINES)
                .add(Blocks.WEEPING_VINES_PLANT)
                .add(Blocks.WEEPING_VINES)
                .add(Blocks.TWISTING_VINES_PLANT)
                .add(Blocks.TWISTING_VINES)
                .add(Blocks.VINE);

        getOrCreateTagBuilder(KlaxonConventionalBlockTags.RUBBER)
                .add(KlaxonBlocks.RUBBER_BLOCK)
                .add(KlaxonBlocks.MOLTEN_RUBBER_BLOCK)
                .add(KlaxonBlocks.RUBBER_SHEET_BLOCK);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.STEEL)
                .add(KlaxonBlocks.STEEL_BLOCK)
                .add(KlaxonBlocks.STEEL_PLATING_BLOCK)
                .add(KlaxonBlocks.STEEL_TRAPDOOR)
                .add(KlaxonBlocks.STEEL_DOOR)
                .add(KlaxonBlocks.STEEL_CASING);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.CRUDE_STEEL)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK)
                .add(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK)
                .add(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_DOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_CASING);

        // fences + doors + trapdoors + pressure plates + buttons + etc...
        getOrCreateTagBuilder(ConventionalBlockTags.WOODEN_FENCES)
                .add(KlaxonBlocks.HALLNOX_FENCE);
        getOrCreateTagBuilder(ConventionalBlockTags.WOODEN_FENCE_GATES)
                .add(KlaxonBlocks.HALLNOX_FENCE_GATE);
        getOrCreateTagBuilder(BlockTags.DOORS)
                .forceAddTag(KlaxonConventionalBlockTags.METAL_DOORS);
        getOrCreateTagBuilder(BlockTags.TRAPDOORS)
                .forceAddTag(KlaxonConventionalBlockTags.METAL_TRAPDOORS);
        getOrCreateTagBuilder(BlockTags.WOODEN_BUTTONS)
                .add(KlaxonBlocks.HALLNOX_BUTTON);
        getOrCreateTagBuilder(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(KlaxonBlocks.HALLNOX_PRESSURE_PLATE);
        getOrCreateTagBuilder(BlockTags.WOODEN_DOORS)
                .add(KlaxonBlocks.HALLNOX_DOOR);
        getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS)
                .add(KlaxonBlocks.HALLNOX_TRAPDOOR);
        getOrCreateTagBuilder(BlockTags.STANDING_SIGNS)
                .add(KlaxonBlocks.HALLNOX_SIGN);
        getOrCreateTagBuilder(BlockTags.WALL_SIGNS)
                .add(KlaxonBlocks.HALLNOX_WALL_SIGN);
        getOrCreateTagBuilder(BlockTags.CEILING_HANGING_SIGNS)
                .add(KlaxonBlocks.HALLNOX_HANGING_SIGN);
        getOrCreateTagBuilder(BlockTags.WALL_HANGING_SIGNS)
                .add(KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN);

        // planks + stairs + slabs + etc...
        getOrCreateTagBuilder(BlockTags.PLANKS)
                .add(KlaxonBlocks.HALLNOX_PLANKS);
        getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS)
                .add(KlaxonBlocks.HALLNOX_STAIRS);
        getOrCreateTagBuilder(BlockTags.WOODEN_SLABS)
                .add(KlaxonBlocks.HALLNOX_SLAB);

        // logs + wood + leaves...
        getOrCreateTagBuilder(ConventionalBlockTags.STRIPPED_LOGS)
                .add(KlaxonBlocks.STRIPPED_HALLNOX_STEM);
        getOrCreateTagBuilder(ConventionalBlockTags.STRIPPED_WOODS)
                .add(KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE);
        getOrCreateTagBuilder(BlockTags.LOGS)
                .forceAddTag(KlaxonBlockTags.HALLNOX_STEMS);
        getOrCreateTagBuilder(KlaxonBlockTags.HALLNOX_STEMS)
                .add(KlaxonBlocks.HALLNOX_STEM)
                .add(KlaxonBlocks.STRIPPED_HALLNOX_STEM)
                .add(KlaxonBlocks.HALLNOX_HYPHAE)
                .add(KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE);
        getOrCreateTagBuilder(BlockTags.WART_BLOCKS)
                .add(KlaxonBlocks.HALLNOX_WART_BLOCK);

        getOrCreateTagBuilder(KlaxonConventionalBlockTags.METAL_DOORS)
                .add(KlaxonBlocks.STEEL_DOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_DOOR)
                .add(Blocks.COPPER_DOOR)
                .add(Blocks.EXPOSED_COPPER_DOOR)
                .add(Blocks.WEATHERED_COPPER_DOOR)
                .add(Blocks.OXIDIZED_COPPER_DOOR)
                .add(Blocks.WAXED_COPPER_DOOR)
                .add(Blocks.WAXED_EXPOSED_COPPER_DOOR)
                .add(Blocks.WAXED_WEATHERED_COPPER_DOOR)
                .add(Blocks.WAXED_OXIDIZED_COPPER_DOOR);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.METAL_TRAPDOORS)
                .add(KlaxonBlocks.STEEL_TRAPDOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR)
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
                .add(KlaxonBlocks.NETHER_REACTOR_CORE)
                .add(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE);
        getOrCreateTagBuilder(KlaxonBlockTags.BLAST_PROCESSORS)
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
    }

    private void buildToolRequirementTags() {
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .forceAddTag(KlaxonConventionalBlockTags.STEEL)
                .add(KlaxonBlocks.IRON_PLATING_BLOCK)
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK)
                .add(KlaxonBlocks.NETHER_REACTOR_CORE);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .forceAddTag(KlaxonConventionalBlockTags.CRUDE_STEEL)
                .add(KlaxonBlocks.COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE);

        // steel tools mirror diamond tools
        getOrCreateTagBuilder(KlaxonBlockTags.NEEDS_STEEL_TOOL)
                .forceAddTag(BlockTags.NEEDS_DIAMOND_TOOL);
        getOrCreateTagBuilder(KlaxonBlockTags.INCORRECT_FOR_STEEL_TOOL)
                .forceAddTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL);

    }

    private void buildCompatTags() {
        getOrCreateTagBuilder(KlaxonCompatBlockTags.IGNITE_FLINT_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK)
                .add(KlaxonBlocks.STEEL_PLATING_BLOCK)
                .add(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK)
                .add(KlaxonBlocks.STEEL_CASING)
                .add(KlaxonBlocks.CRUDE_STEEL_CASING);
    }
}
