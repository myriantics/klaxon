package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockEntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonBlockEntityTypeTagProvider extends FabricTagProvider<BlockEntityType<?>> {
    public KlaxonBlockEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BLOCK_ENTITY_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(KlaxonBlockEntityTypeTags.NETHER_REACTION_OVERWRITABLE)
                .add(BlockEntityType.BRUSHABLE_BLOCK)
                .add(BlockEntityType.CONDUIT)
                .add(BlockEntityType.BANNER)
                .add(BlockEntityType.COMPARATOR)
                .add(BlockEntityType.DAYLIGHT_DETECTOR)
                .add(BlockEntityType.SKULL)
                .add(BlockEntityType.ENCHANTING_TABLE)
                .add(BlockEntityType.ENDER_CHEST);
    }
}
