package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;

import java.util.ArrayList;

public class KlaxonModelProvider extends FabricModelProvider {
    public KlaxonModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerSimpleBlockStateModels(blockStateModelGenerator);
        registerMachineBlockStateModels(blockStateModelGenerator);
        registerMiscBlockStateModels(blockStateModelGenerator);
    }

    private void registerSimpleBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(KlaxonBlocks.STEEL_BLOCK);
        generator.registerSimpleCubeAll(KlaxonBlocks.CRUDE_STEEL_BLOCK);
    }

    private void registerMiscBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerDoor(KlaxonBlocks.CRUDE_STEEL_DOOR);
        generator.registerDoor(KlaxonBlocks.STEEL_DOOR);
        generator.registerOrientableTrapdoor(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR);
        generator.registerOrientableTrapdoor(KlaxonBlocks.STEEL_TRAPDOOR);

        registerPlatingBlock(generator, KlaxonBlocks.STEEL_PLATING_BLOCK);
        registerPlatingBlock(generator, KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK);
        registerPlatingBlock(generator, KlaxonBlocks.IRON_PLATING_BLOCK);
        registerPlatingBlock(generator, KlaxonBlocks.GOLD_PLATING_BLOCK);

        // oxidization is so fun :D
        registerPlatingBlock(generator, KlaxonBlocks.COPPER_PLATING_BLOCK);
        registerPlatingBlock(generator, KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK);
        registerPlatingBlock(generator, KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK);
        registerPlatingBlock(generator, KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK);
        registerPlatingBlock(generator, KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK);
        registerPlatingBlock(generator, KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK);
        registerPlatingBlock(generator, KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK);
        registerPlatingBlock(generator, KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);

        registerPlatingBlock(generator, KlaxonBlocks.ROLLED_RUBBER_BLOCK);
    }

    private void registerMachineBlockStateModels(BlockStateModelGenerator generator) {
        registerDeepslateBlastProcessor(generator);
    }

    private void registerDeepslateBlastProcessor(BlockStateModelGenerator generator) {
        BlockStateVariant empty_closed_lit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "_empty_closed_lit"));
        BlockStateVariant empty_closed_unlit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "_empty_closed_unlit"));
        BlockStateVariant empty_open_lit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "_empty_open_lit"));
        BlockStateVariant empty_open_unlit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "_empty_open_unlit"));
        BlockStateVariant fueled_closed_lit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "_fueled_closed_lit"));
        BlockStateVariant fueled_closed_unlit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "_fueled_closed_unlit"));
        BlockStateVariant fueled_open_lit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "_fueled_open_lit"));
        BlockStateVariant fueled_open_unlit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "_fueled_open_unlit"));

        generator.registerParentedItemModel(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "_empty_open_unlit"));

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)
                .coordinate(BlockStateVariantMap.create(KlaxonBlockStateProperties.FUELED, KlaxonBlockStateProperties.HATCH_OPEN, Properties.LIT)
                        .register(false, false, true, empty_closed_lit)
                        .register(false, false, false, empty_closed_unlit)
                        .register(false, true, true, empty_open_lit)
                        .register(false, true, false, empty_open_unlit)
                        .register(true, false, true, fueled_closed_lit)
                        .register(true, false, false, fueled_closed_unlit)
                        .register(true, true, true, fueled_open_lit)
                        .register(true, true, false, fueled_open_unlit)
                )
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
        );
    }

    private void registerPlatingBlock(BlockStateModelGenerator generator, Block platingBlock) {
        generator.registerAxisRotated(platingBlock, TexturedModel.CUBE_COLUMN);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        // remove any items that have custom model processing here

        // generate all simple item models for stuff defined in list in klaxonitems so i dont have to poll the registry
        generateSimpleItemModels(itemModelGenerator, KlaxonItems.simpleItems);
    }

    private void generateSimpleItemModels(ItemModelGenerator itemModelGenerator, ArrayList<Item> simpleItems) {
        for (Item item : simpleItems) {
            itemModelGenerator.register(item, Models.GENERATED);
        }
    }

    private static Identifier getNestedBlockSubModelId(Block block, String suffix) {
        Identifier identifier = Registries.BLOCK.getId(block);
        return identifier.withPath((path) -> {
            return "block/" + identifier.getPath() + "/" + path + suffix;
        });
    }
}
