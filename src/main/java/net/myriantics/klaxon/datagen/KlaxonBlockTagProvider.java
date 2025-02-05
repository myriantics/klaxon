package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.util.KlaxonTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public KlaxonBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {

        getOrCreateTagBuilder(KlaxonTags.Blocks.HAMMER_MINEABLE)
                .addOptionalTag(KlaxonTags.Blocks.HAMMER_INSTABREAKABLE);

        getOrCreateTagBuilder(KlaxonTags.Blocks.HAMMER_INSTABREAKABLE)
                .addOptionalTag(ConventionalBlockTags.GLASS_BLOCKS)
                .addOptionalTag(ConventionalBlockTags.GLASS_PANES)
                .addOptionalTag(BlockTags.ICE)
                .add(Blocks.REDSTONE_LAMP)
                .add(Blocks.SEA_LANTERN)
                .add(Blocks.GLOWSTONE);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)
                .add(KlaxonBlocks.STEEL_BLOCK)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK);

        getOrCreateTagBuilder(BlockTags.BEACON_BASE_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(KlaxonBlocks.STEEL_BLOCK)
                .add(KlaxonBlocks.CRUDE_STEEL_BLOCK);

        getOrCreateTagBuilder(KlaxonTags.Blocks.MACHINE_MUFFLING_BLOCKS)
                .forceAddTag(BlockTags.WOOL)
                .add(Blocks.HAY_BLOCK)
                .add(Blocks.TARGET);
    }
}
