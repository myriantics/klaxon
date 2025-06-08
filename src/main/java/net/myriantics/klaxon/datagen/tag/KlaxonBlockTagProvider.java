package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
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

        // hoe
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .forceAddTag(KlaxonConventionalBlockTags.RUBBER);
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
                .add(KlaxonBlocks.STEEL_DOOR);
        getOrCreateTagBuilder(KlaxonConventionalBlockTags.CRUDE_STEEL)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK)
                .add(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK)
                .add(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_DOOR);

        getOrCreateTagBuilder(BlockTags.DOORS)
                .forceAddTag(KlaxonConventionalBlockTags.METAL_DOORS);
        getOrCreateTagBuilder(BlockTags.TRAPDOORS)
                .forceAddTag(KlaxonConventionalBlockTags.METAL_TRAPDOORS);

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
                .forceAddTag(KlaxonBlockTags.BLAST_PROCESSORS);
        getOrCreateTagBuilder(KlaxonBlockTags.BLAST_PROCESSORS)
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
    }

    private void buildToolRequirementTags() {
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .forceAddTag(KlaxonConventionalBlockTags.STEEL)
                .add(KlaxonBlocks.IRON_PLATING_BLOCK)
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .forceAddTag(KlaxonConventionalBlockTags.CRUDE_STEEL)
                .add(KlaxonBlocks.COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK)
                .add(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);

        // steel tools mirror diamond tools
        getOrCreateTagBuilder(KlaxonBlockTags.NEEDS_STEEL_TOOL)
                .forceAddTag(BlockTags.NEEDS_DIAMOND_TOOL);
        getOrCreateTagBuilder(KlaxonBlockTags.INCORRECT_FOR_STEEL_TOOL)
                .forceAddTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL);

    }
}
