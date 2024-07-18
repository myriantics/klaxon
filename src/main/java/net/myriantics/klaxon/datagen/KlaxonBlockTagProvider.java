package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.AnvilScreenHandler;
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
                .addOptionalTag(KlaxonTags.Blocks.GLASS_BLOCKS)
                .addOptionalTag(KlaxonTags.Blocks.GLASS_PANES);
                
    }
}
