package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
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
                .add(Blocks.DAMAGED_ANVIL);

        getOrCreateTagBuilder(KlaxonTags.Blocks.HAMMER_MINEABLE)
                .addOptionalTag(KlaxonTags.Blocks.HAMMER_INSTABREAK);

        getOrCreateTagBuilder(KlaxonTags.Blocks.HAMMER_INSTABREAK)
                .addOptionalTag(TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "glass_blocks")))
                .addOptionalTag(TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "glass_panes")))
                .addOptionalTag(BlockTags.ICE);

        getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier("minecraft", "mineable/pickaxe")))
                .add(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);

    }
}
