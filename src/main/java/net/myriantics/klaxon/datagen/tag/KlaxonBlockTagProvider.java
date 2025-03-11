package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.myriantics.klaxon.registry.KlaxonBlocks;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public KlaxonBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {

        // mining-related tags
        getOrCreateTagBuilder(KlaxonBlockTags.HAMMER_MINEABLE)
                .addOptionalTag(KlaxonBlockTags.HAMMER_INSTABREAKABLE)
                .add(Blocks.BEACON);
        getOrCreateTagBuilder(KlaxonBlockTags.HAMMER_INSTABREAKABLE)
                .addOptionalTag(ConventionalBlockTags.GLASS_BLOCKS)
                .addOptionalTag(ConventionalBlockTags.GLASS_PANES)
                .addOptionalTag(ConventionalBlockTags.BUDS)
                .addOptionalTag(BlockTags.ICE)
                .add(Blocks.REDSTONE_LAMP)
                .add(Blocks.SEA_LANTERN)
                .add(Blocks.GLOWSTONE);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)
                .add(KlaxonBlocks.STEEL_BLOCK)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK)
                .add(KlaxonBlocks.STEEL_DOOR)
                .add(KlaxonBlocks.STEEL_TRAPDOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_DOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR)
                .add(KlaxonBlocks.STEEL_PLATING_BLOCK)
                .add(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK)
                .add(KlaxonBlocks.IRON_PLATING_BLOCK)
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK)
                .add(KlaxonBlocks.COPPER_PLATING_BLOCK);

        // tool strength tags
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(KlaxonBlocks.STEEL_BLOCK)
                .add(KlaxonBlocks.STEEL_DOOR)
                .add(KlaxonBlocks.STEEL_TRAPDOOR)
                .add(KlaxonBlocks.STEEL_PLATING_BLOCK)
                .add(KlaxonBlocks.IRON_PLATING_BLOCK)
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK)
                .add(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_DOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK)
                .add(KlaxonBlocks.COPPER_PLATING_BLOCK);

        // behavior related tags
        getOrCreateTagBuilder(BlockTags.BEACON_BASE_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK);
        getOrCreateTagBuilder(BlockTags.GUARDED_BY_PIGLINS)
                .add(KlaxonBlocks.GOLD_PLATING_BLOCK);

        // machine category tags
        getOrCreateTagBuilder(KlaxonBlockTags.BLAST_PROCESSORS)
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);

        // common tags
        getOrCreateTagBuilder(BlockTags.DOORS)
                .add(KlaxonBlocks.STEEL_DOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_DOOR);
        getOrCreateTagBuilder(BlockTags.TRAPDOORS)
                .add(KlaxonBlocks.STEEL_TRAPDOOR)
                .add(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR);
    }
}
