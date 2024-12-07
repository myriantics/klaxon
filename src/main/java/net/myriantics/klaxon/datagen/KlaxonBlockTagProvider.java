package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.AnvilScreenHandler;
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
        getOrCreateTagBuilder(KlaxonTags.Blocks.HAMMER_INTERACTION_POINT)
                .add(Blocks.NETHERITE_BLOCK)
                .add(Blocks.IRON_BLOCK)
                .add(Blocks.SMITHING_TABLE)
                .add(Blocks.ANVIL)
                .add(Blocks.CHIPPED_ANVIL)
                .add(Blocks.DAMAGED_ANVIL)
                .add(KlaxonBlocks.STEEL_BLOCK);

        getOrCreateTagBuilder(KlaxonTags.Blocks.HAMMER_MINEABLE)
                .addOptionalTag(KlaxonTags.Blocks.HAMMER_INSTABREAK);

        getOrCreateTagBuilder(KlaxonTags.Blocks.HAMMER_INSTABREAK)
                .addOptionalTag(TagKey.of(RegistryKeys.BLOCK, Identifier.of("c", "glass_blocks")))
                .addOptionalTag(TagKey.of(RegistryKeys.BLOCK, Identifier.of("c", "glass_panes")))
                .addOptionalTag(BlockTags.ICE)
                .add(Blocks.REDSTONE_LAMP)
                .add(Blocks.SEA_LANTERN)
                .add(Blocks.GLOWSTONE);

        getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "mineable/pickaxe")))
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)
                .add(KlaxonBlocks.STEEL_BLOCK);

        getOrCreateTagBuilder(BlockTags.BEACON_BASE_BLOCKS)
                .add(KlaxonBlocks.STEEL_BLOCK);

        getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "needs_iron_tool")))
                .add(KlaxonBlocks.STEEL_BLOCK);

        getOrCreateTagBuilder(KlaxonTags.Blocks.MACHINE_MUFFLERS)
                .forceAddTag(BlockTags.WOOL)
                .add(Blocks.HAY_BLOCK)
                .add(Blocks.TARGET);
    }
}
