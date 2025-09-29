package net.myriantics.klaxon.datagen.model;

import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.block.customblocks.decor.HallnoxBulbBlock;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeSubProvider;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonBlockModelSubProvider {
    public final BlockStateModelGenerator generator;
    public final KlaxonModelProvider provider;

    public KlaxonBlockModelSubProvider(KlaxonModelProvider provider, BlockStateModelGenerator generator) {
        this.provider = provider;
        this.generator = generator;
    }

    public abstract void generateModels();


    protected void registerSimpleCubeAll(Block block) {
        generator.registerSimpleCubeAll(block);
    }

    protected void registerOxidizedPillarBlock(Block platingBlock, Block modelBlock) {
        Identifier modelIdentifier = ModelIds.getBlockModelId(modelBlock);
        Identifier platingIdentifier = ModelIds.getBlockModelId(platingBlock);
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(platingBlock, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifier)).coordinate(BlockStateModelGenerator.createAxisRotatedVariantMap()));
        generator.modelCollector.accept(platingIdentifier, new SimpleModelSupplier(modelIdentifier));
        generator.registerParentedItemModel(platingBlock, platingIdentifier);
    }

    protected void registerPillarBlock(Block platingBlock) {
        generator.registerAxisRotated(platingBlock, TexturedModel.CUBE_COLUMN);
    }

    protected BlockStateModelGenerator.LogTexturePool registerLog(Block block) {
        return generator.registerLog(block);
    }

    protected void registerDoor(Block doorBlock) {
        generator.registerDoor(doorBlock);
    }

    protected void registerOrientableTrapdoor(Block trapdoorBlock) {
        generator.registerOrientableTrapdoor(trapdoorBlock);
    }

    protected void registerHangingSign(Block strippedLog, Block hangingSign, Block wallHangingSign) {
        generator.registerHangingSign(strippedLog, hangingSign, wallHangingSign);
    }

    protected void registerDeepslateBlastProcessor() {
        BlockStateVariant empty_closed_lit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "empty_closed_lit"));
        BlockStateVariant empty_closed_unlit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "empty_closed_unlit"));
        BlockStateVariant empty_open_lit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "empty_open_lit"));
        BlockStateVariant empty_open_unlit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "empty_open_unlit"));
        BlockStateVariant fueled_closed_lit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "fueled_closed_lit"));
        BlockStateVariant fueled_closed_unlit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "fueled_closed_unlit"));
        BlockStateVariant fueled_open_lit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "fueled_open_lit"));
        BlockStateVariant fueled_open_unlit = BlockStateVariant.create().put(VariantSettings.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "fueled_open_unlit"));

        generator.registerParentedItemModel(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "empty_open_unlit"));

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

    protected void registerHallnoxBulb(HallnoxBulbBlock hallnoxBulb) {
        Identifier bulbPartId =  getNestedBlockSubModelId(hallnoxBulb, "bulb_part");
        Identifier connectorPartId = getNestedBlockSubModelId(hallnoxBulb, "connector_part");

        generator.registerParentedItemModel(hallnoxBulb, bulbPartId);

        generator.blockStateCollector.accept(
                MultipartBlockStateSupplier.create(hallnoxBulb)
                        .with(BlockStateVariant.create().put(VariantSettings.MODEL, bulbPartId))
                        .with(
                                When.create().set(HallnoxBulbBlock.UP, true),
                                BlockStateVariant.create()
                                        .put(VariantSettings.MODEL, connectorPartId)
                                        .put(VariantSettings.X, VariantSettings.Rotation.R270)
                        )                        .with(
                                When.create().set(HallnoxBulbBlock.DOWN, true),
                                BlockStateVariant.create()
                                        .put(VariantSettings.MODEL, connectorPartId)
                                        .put(VariantSettings.X, VariantSettings.Rotation.R90)
                        )
                        .with(
                                When.create().set(HallnoxBulbBlock.NORTH, true),
                                BlockStateVariant.create()
                                        .put(VariantSettings.MODEL, connectorPartId)
                        )
                        .with(
                                When.create().set(HallnoxBulbBlock.EAST, true),
                                BlockStateVariant.create()
                                        .put(VariantSettings.MODEL, connectorPartId)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                        )
                        .with(
                                When.create().set(HallnoxBulbBlock.SOUTH, true),
                                BlockStateVariant.create()
                                        .put(VariantSettings.MODEL, connectorPartId)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                        )
                        .with(
                                When.create().set(HallnoxBulbBlock.WEST, true),
                                BlockStateVariant.create()
                                        .put(VariantSettings.MODEL, connectorPartId)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                        )
        );
    }

    protected void registerHallnoxPod(BlockStateModelGenerator generator) {
        // hallnox pod uses 2d item texture
        generator.registerItemModel(KlaxonItems.HALLNOX_POD);
        Identifier modelId = ModelIds.getBlockModelId(KlaxonBlocks.HALLNOX_POD);
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(KlaxonBlocks.HALLNOX_POD,
                        BlockStateVariant.create().put(VariantSettings.MODEL, modelId)
                )
                .coordinate(createDownDefaultRotationStates()));
    }

    public static BlockStateVariantMap createDownDefaultRotationStates() {
        return BlockStateVariantMap.create(Properties.FACING)
                .register(Direction.UP, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
                .register(Direction.DOWN, BlockStateVariant.create())
                .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270))
                .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
                .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90));
    }

    protected BlockStateModelGenerator.BlockTexturePool registerCubeAllModelTexturePool(Block baseBlock) {
        return generator.registerCubeAllModelTexturePool(baseBlock);
    }

    protected void acceptSingletonBlockState(Block block, Identifier identifier) {
        generator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, identifier));
    }

    private static Identifier getNestedBlockSubModelId(Block block, String suffix) {
        Identifier identifier = Registries.BLOCK.getId(block);
        return identifier.withPath((path) -> "block/" + path + "/" + suffix);
    }
}
