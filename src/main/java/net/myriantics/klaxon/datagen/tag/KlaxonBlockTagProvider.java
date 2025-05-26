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
                .add(Blocks.GLOWSTONE);

        // cable shears
        getOrCreateTagBuilder(KlaxonBlockTags.CABLE_SHEARS_MINEABLE)
                .forceAddTag(KlaxonBlockTags.CABLE_SHEARS_INSTABREAKABLE)
                .add(Blocks.COBWEB);
        getOrCreateTagBuilder(KlaxonBlockTags.CABLE_SHEARS_INSTABREAKABLE)
                .forceAddTag(KlaxonConventionalBlockTags.VINES)
                .forceAddTag(ConventionalBlockTags.CHAINS)
                .forceAddTag(ConventionalBlockTags.ROPES)
                .forceAddTag(BlockTags.CEILING_HANGING_SIGNS)
                .forceAddTag(BlockTags.WOOL_CARPETS)
                .forceAddTag(BlockTags.WOOL)
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
                .add(Blocks.SHROOMLIGHT)
                .add(Blocks.MELON)
                .add(Blocks.BAMBOO)
                .add(Blocks.HONEYCOMB_BLOCK)
                .add(Blocks.GLOW_LICHEN)
                .add(Blocks.CAKE);

        // wrench
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_MINEABLE)
                .forceAddTag(KlaxonBlockTags.WRENCH_INSTABREAKABLE);
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_INSTABREAKABLE)
                .forceAddTag(BlockTags.RAILS)
                .add(Blocks.BLAST_FURNACE)
                .add(Blocks.SHULKER_BOX)
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
                .add(KlaxonBlocks.COPPER_PLATING_BLOCK);

        // hoe
        getOrCreateTagBuilder(BlockTags.HOE_MINEABLE)
                .forceAddTag(KlaxonConventionalBlockTags.RUBBER);
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
                .add(KlaxonBlocks.STEEL_DOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_DOOR);
        getOrCreateTagBuilder(BlockTags.TRAPDOORS)
                .add(KlaxonBlocks.STEEL_TRAPDOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR);

        getOrCreateTagBuilder(KlaxonBlockTags.MACHINES)
                .forceAddTag(KlaxonBlockTags.BLAST_PROCESSORS);
        getOrCreateTagBuilder(KlaxonBlockTags.BLAST_PROCESSORS)
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
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
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_PICKUPABLE)
                .forceAddTag(KlaxonBlockTags.WRENCH_INSTABREAKABLE)
                .add(Blocks.REDSTONE_LAMP)
                .add(Blocks.REPEATER)
                .add(Blocks.COMPARATOR);
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_INTERACTION_DENYLIST)
                .forceAddTag(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED);
        getOrCreateTagBuilder(KlaxonBlockTags.WRENCH_ROTATABLE)
                .forceAddTag(KlaxonBlockTags.WRENCH_INSTABREAKABLE)
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)
                .add(Blocks.DISPENSER)
                .add(Blocks.DROPPER);
    }

    private void buildToolRequirementTags() {
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .forceAddTag(KlaxonConventionalBlockTags.STEEL)
                .add(KlaxonBlocks.IRON_PLATING_BLOCK)
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .forceAddTag(KlaxonConventionalBlockTags.CRUDE_STEEL)
                .add(KlaxonBlocks.COPPER_PLATING_BLOCK);

        // steel tools mirror diamond tools
        getOrCreateTagBuilder(KlaxonBlockTags.NEEDS_STEEL_TOOL)
                .forceAddTag(BlockTags.NEEDS_DIAMOND_TOOL);
        getOrCreateTagBuilder(KlaxonBlockTags.INCORRECT_FOR_STEEL_TOOL)
                .forceAddTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL);

    }
}
