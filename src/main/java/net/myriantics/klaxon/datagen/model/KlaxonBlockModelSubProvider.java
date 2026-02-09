package net.myriantics.klaxon.datagen.model;

import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.block.decor.hallnox_bulb.HallnoxBulbBlock;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.PipeMatrixUBendBlock;
import net.myriantics.klaxon.block.machines.nether_reactor_core.NetherReactorCoreBlock;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.render.KlaxonModels;
import net.myriantics.klaxon.registry.render.KlaxonTextureKeys;

import java.util.Map;

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

    protected void registerPipeMatrixSegment(Block segmentBlock, Item pipeMatrixItem) {
        this.registerPipeMatrixSegment(segmentBlock, pipeMatrixItem, pipeMatrixItem);
    }

    protected void registerPipeMatrixSegment(Block segmentBlock, Item pipeMatrixItem, Item pipeMatrixItemThatIndicatesTextureDirectory) {
        Identifier modelIdentifier = ModelIds.getBlockModelId(segmentBlock);
        Identifier itemId = Registries.ITEM.getId(pipeMatrixItem);
        String texturePath = Registries.ITEM.getId(pipeMatrixItemThatIndicatesTextureDirectory).getPath();

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(segmentBlock, BlockStateVariant.create().put(VariantSettings.MODEL, modelIdentifier)).coordinate(BlockStateModelGenerator.createAxisRotatedVariantMap()));
        generator.modelCollector.accept(
                modelIdentifier,
                () -> Models.CUBE_BOTTOM_TOP.createJson(
                        modelIdentifier,
                        Map.of(
                                TextureKey.TOP, itemId.withPath((path) -> "block/" + texturePath + "/exposed_pipes"),
                                TextureKey.BOTTOM, itemId.withPath((path) -> "block/" + texturePath + "/exposed_pipes"),
                                TextureKey.SIDE, itemId.withPath((path) -> "block/" + texturePath + "/segment_side")
                        )
                )
        );

        generator.registerParentedItemModel(pipeMatrixItem, modelIdentifier);
    }

    protected void registerPipeMatrixUBend(Block uBendBlock, Item pipeMatrixItemThatIndicatesTextureDirectory) {
        Identifier itemId = Registries.ITEM.getId(pipeMatrixItemThatIndicatesTextureDirectory);

        Identifier baseModelIdentifier = ModelIds.getBlockModelId(uBendBlock);
        Identifier xAxisPositiveModelIdentifier = baseModelIdentifier.withPath((path) -> path + "/x_positive");
        Identifier xAxisNegativeModelIdentifier = baseModelIdentifier.withPath((path) -> path + "/x_negative");
        Identifier zAxisPositiveModelIdentifier = baseModelIdentifier.withPath((path) -> path + "/z_positive");
        Identifier zAxisNegativeModelIdentifier = baseModelIdentifier.withPath((path) -> path + "/z_negative");

        Identifier topId = itemId.withPath((path) -> "block/" + path + "/exposed_pipes");
        Identifier positiveUBendId = itemId.withPath((path) -> "block/" + path + "/u_bend_curve_positive");
        Identifier negativeUBendId = itemId.withPath((path) -> "block/" + path + "/u_bend_curve_negative");
        Identifier bottomId = itemId.withPath((path) -> "block/" + path + "/u_bend_bottom");
        Identifier positiveSideId = itemId.withPath((path) -> "block/" + path + "/u_bend_side_positive");
        Identifier negativeSideId = itemId.withPath((path) -> "block/" + path + "/u_bend_side_negative");

        Map<TextureKey, Identifier> positiveTextureMap = Map.of(
                TextureKey.TOP, topId,
                KlaxonTextureKeys.U_BEND_CURVE, positiveUBendId,
                KlaxonTextureKeys.U_BEND_BOTTOM, bottomId,
                KlaxonTextureKeys.U_BEND_SIDE, positiveSideId,
                TextureKey.PARTICLE, positiveSideId
        );
        Map<TextureKey, Identifier> negativeTextureMap = Map.of(
                TextureKey.BOTTOM, topId,
                KlaxonTextureKeys.U_BEND_CURVE, negativeUBendId,
                KlaxonTextureKeys.U_BEND_BOTTOM, bottomId,
                KlaxonTextureKeys.U_BEND_SIDE, negativeSideId,
                TextureKey.PARTICLE, negativeSideId
        );

        // create both x and z variants
        generator.modelCollector.accept(
                xAxisPositiveModelIdentifier,
                () -> KlaxonModels.PIPE_MATRIX_U_BEND_X_POSITIVE.createJson(xAxisPositiveModelIdentifier, positiveTextureMap)
        );
        generator.modelCollector.accept(
                zAxisPositiveModelIdentifier,
                () -> KlaxonModels.PIPE_MATRIX_U_BEND_Z_POSITIVE.createJson(zAxisPositiveModelIdentifier, positiveTextureMap)
        );
        generator.modelCollector.accept(
                xAxisNegativeModelIdentifier,
                () -> KlaxonModels.PIPE_MATRIX_U_BEND_X_NEGATIVE.createJson(xAxisNegativeModelIdentifier, negativeTextureMap)
        );
        generator.modelCollector.accept(
                zAxisNegativeModelIdentifier,
                () -> KlaxonModels.PIPE_MATRIX_U_BEND_Z_NEGATIVE.createJson(zAxisNegativeModelIdentifier, negativeTextureMap)
        );

        // make the map - helpful comment i know :)
        BlockStateVariantMap map = BlockStateVariantMap.create(PipeMatrixUBendBlock.FACING, PipeMatrixUBendBlock.HORIZONTAL_AXIS)
                .register(
                        Direction.UP, Direction.Axis.X,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, xAxisPositiveModelIdentifier)
                )
                .register(
                        Direction.UP, Direction.Axis.Z,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, zAxisPositiveModelIdentifier)
                )
                .register(
                        Direction.DOWN, Direction.Axis.X,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, xAxisNegativeModelIdentifier)
                )
                .register(
                        Direction.DOWN, Direction.Axis.Z,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, zAxisNegativeModelIdentifier)
                )
                .register(
                        Direction.NORTH, Direction.Axis.X,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, xAxisPositiveModelIdentifier)
                                .put(VariantSettings.X, VariantSettings.Rotation.R270)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                )
                .register(
                        Direction.NORTH, Direction.Axis.Z,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, zAxisPositiveModelIdentifier)
                                .put(VariantSettings.X, VariantSettings.Rotation.R270)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                )
                .register(
                        Direction.EAST, Direction.Axis.X,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, zAxisPositiveModelIdentifier)
                                .put(VariantSettings.X, VariantSettings.Rotation.R90)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                )
                .register(
                        Direction.EAST, Direction.Axis.Z,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, xAxisPositiveModelIdentifier)
                                .put(VariantSettings.X, VariantSettings.Rotation.R90)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                )
                .register(
                        Direction.SOUTH, Direction.Axis.X,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, xAxisNegativeModelIdentifier)
                                .put(VariantSettings.X, VariantSettings.Rotation.R90)
                )
                .register(
                        Direction.SOUTH, Direction.Axis.Z,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, zAxisNegativeModelIdentifier)
                                .put(VariantSettings.X, VariantSettings.Rotation.R90)
                )
                .register(
                        Direction.WEST, Direction.Axis.X,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, zAxisNegativeModelIdentifier)
                                .put(VariantSettings.X, VariantSettings.Rotation.R90)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                )
                .register(
                        Direction.WEST, Direction.Axis.Z,
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, xAxisNegativeModelIdentifier)
                                .put(VariantSettings.X, VariantSettings.Rotation.R90)
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                );

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(uBendBlock).coordinate(map));
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

    protected void registerNetherReactorCore(Block block) {
        Identifier normalModelIdentifier = ModelIds.getBlockModelId(block);
        Identifier rotatedModelIdentifier = normalModelIdentifier.withPath((path) -> path + "_rotated");
        Identifier blockId = Registries.BLOCK.getId(block);

        BlockStateVariantMap map = BlockStateVariantMap.create(NetherReactorCoreBlock.HORIZONTAL_AXIS)
                .register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, normalModelIdentifier))
                .register(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, rotatedModelIdentifier));

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(map));

        Map<TextureKey, Identifier> textureMap = Map.of(
                KlaxonTextureKeys.CASING, blockId.withPath((path) -> "block/" + path + "/casing"),
                KlaxonTextureKeys.CORE, blockId.withPath((path) -> "block/" + path + "/core"),
                TextureKey.PARTICLE, blockId.withPath((path) -> "block/" + path + "/casing")
        );

        generator.modelCollector.accept(
                normalModelIdentifier,
                () -> KlaxonModels.NORMAL_NETHER_REACTOR_CORE.createJson(
                        normalModelIdentifier,
                        textureMap
                )
        );
        generator.modelCollector.accept(
                rotatedModelIdentifier,
                () -> KlaxonModels.ROTATED_NETHER_REACTOR_CORE.createJson(
                        rotatedModelIdentifier,
                        textureMap
                )
        );

        generator.registerParentedItemModel(block, normalModelIdentifier);
    }

    protected void acceptSingletonBlockState(Block block, Identifier identifier) {
        generator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, identifier));
    }

    private static Identifier getNestedBlockSubModelId(Block block, String suffix) {
        Identifier identifier = Registries.BLOCK.getId(block);
        return identifier.withPath((path) -> "block/" + path + "/" + suffix);
    }
}
