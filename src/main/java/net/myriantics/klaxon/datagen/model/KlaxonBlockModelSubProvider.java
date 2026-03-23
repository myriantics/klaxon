package net.myriantics.klaxon.datagen.model;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.klaxon.block.decor.hallnox_bulb.HallnoxBulbBlock;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.PipeMatrixUBendBlock;
import net.myriantics.klaxon.block.machines.nether_reactor_core.NetherReactorCoreBlock;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.render.KlaxonModels;
import net.myriantics.klaxon.registry.render.KlaxonTextureKeys;
import net.myriantics.klaxon.registry.render.KlaxonTextures;

import java.util.Map;

public abstract class KlaxonBlockModelSubProvider {
    public final BlockModelGenerators generator;
    public final KlaxonModelProvider provider;

    public KlaxonBlockModelSubProvider(KlaxonModelProvider provider, BlockModelGenerators generator) {
        this.provider = provider;
        this.generator = generator;
    }

    public abstract void generateModels();


    protected void registerSimpleCubeAll(Block block) {
        generator.createTrivialCube(block);
    }

    protected void registerOxidizedPillarBlock(Block platingBlock, Block modelBlock) {
        ResourceLocation modelIdentifier = ModelLocationUtils.getModelLocation(modelBlock);
        ResourceLocation platingIdentifier = ModelLocationUtils.getModelLocation(platingBlock);
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(platingBlock, Variant.variant().with(VariantProperties.MODEL, modelIdentifier)).with(BlockModelGenerators.createRotatedPillar()));
        generator.modelOutput.accept(platingIdentifier, new DelegatedModel(modelIdentifier));
        generator.delegateItemModel(platingBlock, platingIdentifier);
    }

    protected void registerPillarBlock(Block platingBlock) {
        generator.createAxisAlignedPillarBlock(platingBlock, TexturedModel.COLUMN);
    }

    protected BlockModelGenerators.WoodProvider registerLog(Block block) {
        return generator.woodProvider(block);
    }

    protected void registerDoor(Block doorBlock) {
        generator.createDoor(doorBlock);
    }

    protected void registerDoor(Holder<Block> holder) {
        registerDoor(holder.value());
    }

    protected void registerOrientableTrapdoor(Block trapdoorBlock) {
        generator.createOrientableTrapdoor(trapdoorBlock);
    }

    protected void registerOrientableTrapdoor(Holder<Block> holder) {
        registerOrientableTrapdoor(holder.value());
    }

    protected void registerHangingSign(Holder<Block> strippedLogHolder, Holder<Block> hangingSignHolder, Holder<Block> wallHangingSignHolder) {
        registerHangingSign(strippedLogHolder.value(), hangingSignHolder.value(), wallHangingSignHolder.value());
    }

    protected void registerHangingSign(Block strippedLog, Block hangingSign, Block wallHangingSign) {
        generator.createHangingSign(strippedLog, hangingSign, wallHangingSign);
    }

    protected void registerSteelWorkbench(Holder<Block> holder) {
        Block block = holder.value();
        ResourceLocation modelId = ModelLocationUtils.getModelLocation(block);

        Map<TextureSlot, ResourceLocation> textureMap = Map.of(
                TextureSlot.TOP, KlaxonTextures.STEEL_WORKBENCH_TOP,
                TextureSlot.SIDE, KlaxonTextures.STEEL_WORKBENCH_SIDE,
                TextureSlot.BOTTOM, KlaxonTextures.STEEL_CASING,
                TextureSlot.PARTICLE, KlaxonTextures.STEEL_WORKBENCH_SIDE
        );

        generator.delegateItemModel(block, modelId);

        generator.modelOutput.accept(
                modelId,
                () -> ModelTemplates.CUBE_BOTTOM_TOP.createBaseTemplate(modelId, textureMap)
        );

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, modelId).with(VariantProperties.UV_LOCK, false)).with(createUpDefaultRotationStates()));
    }

    protected void registerDeepslateBlastProcessor() {
        Variant empty_closed_lit = Variant.variant().with(VariantProperties.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "empty_closed_lit"));
        Variant empty_closed_unlit = Variant.variant().with(VariantProperties.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "empty_closed_unlit"));
        Variant empty_open_lit = Variant.variant().with(VariantProperties.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "empty_open_lit"));
        Variant empty_open_unlit = Variant.variant().with(VariantProperties.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "empty_open_unlit"));
        Variant fueled_closed_lit = Variant.variant().with(VariantProperties.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "fueled_closed_lit"));
        Variant fueled_closed_unlit = Variant.variant().with(VariantProperties.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "fueled_closed_unlit"));
        Variant fueled_open_lit = Variant.variant().with(VariantProperties.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "fueled_open_lit"));
        Variant fueled_open_unlit = Variant.variant().with(VariantProperties.MODEL, getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "fueled_open_unlit"));

        generator.delegateItemModel(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.value(), getNestedBlockSubModelId(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.value(), "empty_open_unlit"));

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.value())
                .with(PropertyDispatch.properties(KlaxonBlockStateProperties.FUELED, KlaxonBlockStateProperties.HATCH_OPEN, BlockStateProperties.LIT)
                        .select(false, false, true, empty_closed_lit)
                        .select(false, false, false, empty_closed_unlit)
                        .select(false, true, true, empty_open_lit)
                        .select(false, true, false, empty_open_unlit)
                        .select(true, false, true, fueled_closed_lit)
                        .select(true, false, false, fueled_closed_unlit)
                        .select(true, true, true, fueled_open_lit)
                        .select(true, true, false, fueled_open_unlit)
                )
                .with(BlockModelGenerators.createHorizontalFacingDispatch())
        );
    }

    protected void registerHallnoxBulb(HallnoxBulbBlock hallnoxBulb) {
        ResourceLocation bulbPartId =  getNestedBlockSubModelId(hallnoxBulb, "bulb_part");
        ResourceLocation connectorPartId = getNestedBlockSubModelId(hallnoxBulb, "connector_part");

        generator.delegateItemModel(hallnoxBulb, bulbPartId);

        generator.blockStateOutput.accept(
                MultiPartGenerator.multiPart(hallnoxBulb)
                        .with(Variant.variant().with(VariantProperties.MODEL, bulbPartId))
                        .with(
                                Condition.condition().term(HallnoxBulbBlock.UP, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, connectorPartId)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                        )                        .with(
                                Condition.condition().term(HallnoxBulbBlock.DOWN, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, connectorPartId)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                        )
                        .with(
                                Condition.condition().term(HallnoxBulbBlock.NORTH, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, connectorPartId)
                        )
                        .with(
                                Condition.condition().term(HallnoxBulbBlock.EAST, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, connectorPartId)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                        )
                        .with(
                                Condition.condition().term(HallnoxBulbBlock.SOUTH, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, connectorPartId)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                        )
                        .with(
                                Condition.condition().term(HallnoxBulbBlock.WEST, true),
                                Variant.variant()
                                        .with(VariantProperties.MODEL, connectorPartId)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                        )
        );
    }

    protected void registerPipeMatrixSegment(Holder<Block> holder, Item pipeMatrixItem) {
        this.registerPipeMatrixSegment(holder, pipeMatrixItem, pipeMatrixItem);
    }

    protected void registerPipeMatrixSegment(Holder<Block> segmentBlockHolder, Item pipeMatrixItem, Item pipeMatrixItemThatIndicatesTextureDirectory) {
        Block segmentBlock = segmentBlockHolder.value();
        ResourceLocation modelIdentifier = ModelLocationUtils.getModelLocation(segmentBlock);
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(pipeMatrixItem);
        String texturePath = BuiltInRegistries.ITEM.getKey(pipeMatrixItemThatIndicatesTextureDirectory).getPath();

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(segmentBlock, Variant.variant().with(VariantProperties.MODEL, modelIdentifier)).with(BlockModelGenerators.createRotatedPillar()));
        generator.modelOutput.accept(
                modelIdentifier,
                () -> ModelTemplates.CUBE_BOTTOM_TOP.createBaseTemplate(
                        modelIdentifier,
                        Map.of(
                                TextureSlot.TOP, itemId.withPath((path) -> "block/" + texturePath + "/exposed_pipes"),
                                TextureSlot.BOTTOM, itemId.withPath((path) -> "block/" + texturePath + "/exposed_pipes"),
                                TextureSlot.SIDE, itemId.withPath((path) -> "block/" + texturePath + "/segment_side")
                        )
                )
        );

        generator.delegateItemModel(pipeMatrixItem, modelIdentifier);
    }

    protected void registerPipeMatrixUBend(Holder<Block> holder, Holder<Item> pipeMatrixItemThatIndicatesTextureDirectoryHolder) {
        Block uBendBlock = holder.value();
        Item pipeMatrixItemThatIndicatesTextureDirectory = pipeMatrixItemThatIndicatesTextureDirectoryHolder.value();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(pipeMatrixItemThatIndicatesTextureDirectory);

        ResourceLocation baseModelIdentifier = ModelLocationUtils.getModelLocation(uBendBlock);
        ResourceLocation xAxisPositiveModelIdentifier = baseModelIdentifier.withPath((path) -> path + "/x_positive");
        ResourceLocation xAxisNegativeModelIdentifier = baseModelIdentifier.withPath((path) -> path + "/x_negative");
        ResourceLocation zAxisPositiveModelIdentifier = baseModelIdentifier.withPath((path) -> path + "/z_positive");
        ResourceLocation zAxisNegativeModelIdentifier = baseModelIdentifier.withPath((path) -> path + "/z_negative");

        ResourceLocation topId = itemId.withPath((path) -> "block/" + path + "/exposed_pipes");
        ResourceLocation positiveUBendId = itemId.withPath((path) -> "block/" + path + "/u_bend_curve_positive");
        ResourceLocation negativeUBendId = itemId.withPath((path) -> "block/" + path + "/u_bend_curve_negative");
        ResourceLocation bottomId = itemId.withPath((path) -> "block/" + path + "/u_bend_bottom");
        ResourceLocation positiveSideId = itemId.withPath((path) -> "block/" + path + "/u_bend_side_positive");
        ResourceLocation negativeSideId = itemId.withPath((path) -> "block/" + path + "/u_bend_side_negative");

        Map<TextureSlot, ResourceLocation> positiveTextureMap = Map.of(
                TextureSlot.TOP, topId,
                KlaxonTextureKeys.U_BEND_CURVE, positiveUBendId,
                KlaxonTextureKeys.U_BEND_BOTTOM, bottomId,
                KlaxonTextureKeys.U_BEND_SIDE, positiveSideId,
                TextureSlot.PARTICLE, positiveSideId
        );
        Map<TextureSlot, ResourceLocation> negativeTextureMap = Map.of(
                TextureSlot.BOTTOM, topId,
                KlaxonTextureKeys.U_BEND_CURVE, negativeUBendId,
                KlaxonTextureKeys.U_BEND_BOTTOM, bottomId,
                KlaxonTextureKeys.U_BEND_SIDE, negativeSideId,
                TextureSlot.PARTICLE, negativeSideId
        );

        // create both x and z variants
        generator.modelOutput.accept(
                xAxisPositiveModelIdentifier,
                () -> KlaxonModels.PIPE_MATRIX_U_BEND_X_POSITIVE.createBaseTemplate(xAxisPositiveModelIdentifier, positiveTextureMap)
        );
        generator.modelOutput.accept(
                zAxisPositiveModelIdentifier,
                () -> KlaxonModels.PIPE_MATRIX_U_BEND_Z_POSITIVE.createBaseTemplate(zAxisPositiveModelIdentifier, positiveTextureMap)
        );
        generator.modelOutput.accept(
                xAxisNegativeModelIdentifier,
                () -> KlaxonModels.PIPE_MATRIX_U_BEND_X_NEGATIVE.createBaseTemplate(xAxisNegativeModelIdentifier, negativeTextureMap)
        );
        generator.modelOutput.accept(
                zAxisNegativeModelIdentifier,
                () -> KlaxonModels.PIPE_MATRIX_U_BEND_Z_NEGATIVE.createBaseTemplate(zAxisNegativeModelIdentifier, negativeTextureMap)
        );

        // make the map - helpful comment i know :)
        PropertyDispatch map = PropertyDispatch.properties(PipeMatrixUBendBlock.FACING, PipeMatrixUBendBlock.HORIZONTAL_AXIS)
                .select(
                        Direction.UP, Direction.Axis.X,
                        Variant.variant()
                                .with(VariantProperties.MODEL, xAxisPositiveModelIdentifier)
                )
                .select(
                        Direction.UP, Direction.Axis.Z,
                        Variant.variant()
                                .with(VariantProperties.MODEL, zAxisPositiveModelIdentifier)
                )
                .select(
                        Direction.DOWN, Direction.Axis.X,
                        Variant.variant()
                                .with(VariantProperties.MODEL, xAxisNegativeModelIdentifier)
                )
                .select(
                        Direction.DOWN, Direction.Axis.Z,
                        Variant.variant()
                                .with(VariantProperties.MODEL, zAxisNegativeModelIdentifier)
                )
                .select(
                        Direction.NORTH, Direction.Axis.X,
                        Variant.variant()
                                .with(VariantProperties.MODEL, xAxisPositiveModelIdentifier)
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                )
                .select(
                        Direction.NORTH, Direction.Axis.Z,
                        Variant.variant()
                                .with(VariantProperties.MODEL, zAxisPositiveModelIdentifier)
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                )
                .select(
                        Direction.EAST, Direction.Axis.X,
                        Variant.variant()
                                .with(VariantProperties.MODEL, zAxisPositiveModelIdentifier)
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                )
                .select(
                        Direction.EAST, Direction.Axis.Z,
                        Variant.variant()
                                .with(VariantProperties.MODEL, xAxisPositiveModelIdentifier)
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                )
                .select(
                        Direction.SOUTH, Direction.Axis.X,
                        Variant.variant()
                                .with(VariantProperties.MODEL, xAxisNegativeModelIdentifier)
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                )
                .select(
                        Direction.SOUTH, Direction.Axis.Z,
                        Variant.variant()
                                .with(VariantProperties.MODEL, zAxisNegativeModelIdentifier)
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                )
                .select(
                        Direction.WEST, Direction.Axis.X,
                        Variant.variant()
                                .with(VariantProperties.MODEL, zAxisNegativeModelIdentifier)
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                )
                .select(
                        Direction.WEST, Direction.Axis.Z,
                        Variant.variant()
                                .with(VariantProperties.MODEL, xAxisNegativeModelIdentifier)
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                );

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(uBendBlock).with(map));
    }

    protected void registerHallnoxPod(BlockModelGenerators generator) {
        // hallnox pod uses 2d item texture
        generator.createSimpleFlatItemModel(KlaxonItems.HALLNOX_POD.value());
        ResourceLocation modelId = ModelLocationUtils.getModelLocation(KlaxonBlocks.HALLNOX_POD.value());
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(KlaxonBlocks.HALLNOX_POD.value(),
                        Variant.variant().with(VariantProperties.MODEL, modelId)
                )
                .with(createDownDefaultRotationStates()));
    }

    public static PropertyDispatch createDownDefaultRotationStates() {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.UP, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(Direction.DOWN, Variant.variant())
                .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                .select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                .select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
    }
    public static PropertyDispatch createUpDefaultRotationStates() {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.UP, Variant.variant())
                .select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                .select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
    }

    protected BlockModelGenerators.BlockFamilyProvider registerCubeAllModelTexturePool(Block baseBlock) {
        return generator.family(baseBlock);
    }

    protected void registerNetherReactorCore(Holder<Block> holder) {
        Block block = holder.value();
        ResourceLocation normalModelIdentifier = ModelLocationUtils.getModelLocation(block);
        ResourceLocation rotatedModelIdentifier = normalModelIdentifier.withPath((path) -> path + "_rotated");
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);

        PropertyDispatch map = PropertyDispatch.property(NetherReactorCoreBlock.HORIZONTAL_AXIS)
                .select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, normalModelIdentifier))
                .select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, rotatedModelIdentifier));

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(map));

        Map<TextureSlot, ResourceLocation> textureMap = Map.of(
                KlaxonTextureKeys.CASING, blockId.withPath((path) -> "block/" + path + "/casing"),
                KlaxonTextureKeys.CORE, blockId.withPath((path) -> "block/" + path + "/core"),
                TextureSlot.PARTICLE, blockId.withPath((path) -> "block/" + path + "/casing")
        );

        generator.modelOutput.accept(
                normalModelIdentifier,
                () -> KlaxonModels.NORMAL_NETHER_REACTOR_CORE.createBaseTemplate(
                        normalModelIdentifier,
                        textureMap
                )
        );
        generator.modelOutput.accept(
                rotatedModelIdentifier,
                () -> KlaxonModels.ROTATED_NETHER_REACTOR_CORE.createBaseTemplate(
                        rotatedModelIdentifier,
                        textureMap
                )
        );

        generator.delegateItemModel(block, normalModelIdentifier);
    }

    protected void acceptSingletonBlockState(Holder<Block> holder, ResourceLocation id) {
        acceptSingletonBlockState(holder.value(), id);
    }

    protected void acceptSingletonBlockState(Block block, ResourceLocation identifier) {
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, identifier));
    }

    private static ResourceLocation getNestedBlockSubModelId(Holder<Block> holder, String suffix) {
        return getNestedBlockSubModelId(holder.value(), suffix);
    }

    private static ResourceLocation getNestedBlockSubModelId(Block block, String suffix) {
        ResourceLocation identifier = BuiltInRegistries.BLOCK.getKey(block);
        return identifier.withPath((path) -> "block/" + path + "/" + suffix);
    }
}
