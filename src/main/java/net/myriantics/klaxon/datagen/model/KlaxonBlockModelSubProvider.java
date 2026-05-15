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
import net.myriantics.klaxon.block.functional.pressure_plate.FaultyHeavyGatedPressurePlateBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorLootState;
import net.myriantics.klaxon.block.machines.geothermal.pipe_matrix.PipeMatrixUBendBlock;
import net.myriantics.klaxon.block.machines.modular_explosive.FuseState;
import net.myriantics.klaxon.block.machines.modular_explosive.ModularExplosiveBlock;
import net.myriantics.klaxon.block.machines.nether_reactor_core.NetherReactorCoreBlock;
import net.myriantics.klaxon.block.machines.precision_dispenser.PrecisionDispenserBlock;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.render.KlaxonModelTemplates;
import net.myriantics.klaxon.registry.render.KlaxonTextureSlots;
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

    protected void registerDeepslateBlastProcessor(Holder<Block> holder) {
        this.registerDeepslateBlastProcessor(holder.value());
    }

    protected void registerPrecisionDispenser(Block block) {
        ResourceLocation baseRl = ModelLocationUtils.getModelLocation(block);
        ResourceLocation horizontalRl = baseRl.withSuffix("/horizontal");
        ResourceLocation verticalRl = baseRl.withSuffix("/vertical");
        ResourceLocation horizontalIdleRl = horizontalRl.withSuffix("/idle");
        ResourceLocation horizontalTriggeredRl = horizontalRl.withSuffix("/triggered");
        ResourceLocation verticalIdleRl = verticalRl.withSuffix("/idle");
        ResourceLocation verticalTriggeredRl = verticalRl.withSuffix("/triggered");

        TextureMapping horizontalIdleTexMap = new TextureMapping()
                .put(TextureSlot.FRONT, horizontalRl.withSuffix("/front_idle"))
                .put(TextureSlot.BACK, baseRl.withSuffix("/back_idle"))
                .put(TextureSlot.SIDE, baseRl.withSuffix("/side_idle"))
                .put(TextureSlot.PARTICLE, baseRl.withSuffix("/side_idle"));
        TextureMapping horizontalTriggeredTexMap = new TextureMapping()
                .put(TextureSlot.FRONT, horizontalRl.withSuffix("/front_triggered"))
                .put(TextureSlot.BACK, baseRl.withSuffix("/back_triggered"))
                .put(TextureSlot.SIDE, baseRl.withSuffix("/side_triggered"))
                .put(TextureSlot.PARTICLE, baseRl.withSuffix("/side_triggered"));
        TextureMapping verticalIdleTexMap = new TextureMapping()
                .put(TextureSlot.TOP, verticalRl.withSuffix("/front_idle"))
                .put(TextureSlot.SIDE, baseRl.withSuffix("/side_idle"))
                .put(TextureSlot.BOTTOM, baseRl.withSuffix("/back_idle"))
                .put(TextureSlot.PARTICLE, baseRl.withSuffix("/side_idle"));
        TextureMapping verticalTriggeredTexMap = new TextureMapping()
                .put(TextureSlot.TOP, verticalRl.withSuffix("/front_triggered"))
                .put(TextureSlot.SIDE, baseRl.withSuffix("/side_triggered"))
                .put(TextureSlot.BOTTOM, baseRl.withSuffix("/back_triggered"))
                .put(TextureSlot.PARTICLE, baseRl.withSuffix("/side_triggered"));

        ResourceLocation verticalTriggered = ModelTemplates.CUBE_BOTTOM_TOP.create(verticalTriggeredRl, verticalTriggeredTexMap, generator.modelOutput);
        ResourceLocation verticalIdle = ModelTemplates.CUBE_BOTTOM_TOP.create(verticalIdleRl, verticalIdleTexMap, generator.modelOutput);
        ResourceLocation horizontalTriggered = KlaxonModelTemplates.CUBE_FRONT_SIDE_BACK.create(horizontalTriggeredRl, horizontalTriggeredTexMap, generator.modelOutput);
        ResourceLocation horizontalIdle = KlaxonModelTemplates.CUBE_FRONT_SIDE_BACK.create(horizontalIdleRl, horizontalIdleTexMap, generator.modelOutput);

        generator.delegateItemModel(block, horizontalIdle);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.properties(PrecisionDispenserBlock.FACING, PrecisionDispenserBlock.TRIGGERED)
                        .select(Direction.UP, true, modelVariant(verticalTriggered))
                        .select(Direction.UP, false, modelVariant(verticalIdle))
                        .select(Direction.DOWN, true, modelVariant(verticalTriggered).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                        .select(Direction.DOWN, false, modelVariant(verticalIdle).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                        .select(Direction.NORTH, true, modelVariant(horizontalTriggered))
                        .select(Direction.NORTH, false, modelVariant(horizontalIdle))
                        .select(Direction.SOUTH, true, modelVariant(horizontalTriggered).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                        .select(Direction.SOUTH, false, modelVariant(horizontalIdle).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                        .select(Direction.EAST, true, modelVariant(horizontalTriggered).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                        .select(Direction.EAST, false, modelVariant(horizontalIdle).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                        .select(Direction.WEST, true, modelVariant(horizontalTriggered).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                        .select(Direction.WEST, false, modelVariant(horizontalIdle).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                )
        );
    }

    protected void registerModularExplosive(Block block) {
        ResourceLocation baseRl = ModelLocationUtils.getModelLocation(block);
        ResourceLocation inertRl = baseRl.withSuffix("/inert");
        ResourceLocation farRl = baseRl.withSuffix("/far");
        ResourceLocation closeRl = baseRl.withSuffix("/close");
        ResourceLocation imminentRl = baseRl.withSuffix("/imminent");

        TextureMapping inertTriggeredTexMap = new TextureMapping()
                .put(KlaxonTextureSlots.TOP_LAYER_0, inertRl.withSuffix("/top_tinted_light"))
                .put(KlaxonTextureSlots.SIDE_LAYER_0, inertRl.withSuffix("/side_tinted_light"))
                .put(KlaxonTextureSlots.TOP_LAYER_1, baseRl.withSuffix("/top_static_triggered"))
                .put(KlaxonTextureSlots.SIDE_LAYER_1, baseRl.withSuffix("/side_static_triggered"))
                .put(KlaxonTextureSlots.BOTTOM_LAYER_1, baseRl.withSuffix("/bottom_static_triggered"))
                .put(TextureSlot.PARTICLE, baseRl.withSuffix("/side_static_triggered"));
        TextureMapping inertIdleTexMap = new TextureMapping()
                .put(KlaxonTextureSlots.TOP_LAYER_0, inertRl.withSuffix("/top_tinted_dark"))
                .put(KlaxonTextureSlots.SIDE_LAYER_0, inertRl.withSuffix("/side_tinted_dark"))
                .put(KlaxonTextureSlots.TOP_LAYER_1, baseRl.withSuffix("/top_static_idle"))
                .put(KlaxonTextureSlots.SIDE_LAYER_1, baseRl.withSuffix("/side_static_idle"))
                .put(KlaxonTextureSlots.BOTTOM_LAYER_1, baseRl.withSuffix("/bottom_static_idle"))
                .put(TextureSlot.PARTICLE, baseRl.withSuffix("/side_static_idle"));
        TextureMapping farTriggeredTexMap = inertTriggeredTexMap
                .copyAndUpdate(KlaxonTextureSlots.TOP_LAYER_0, farRl.withSuffix("/top_tinted"))
                .put(KlaxonTextureSlots.SIDE_LAYER_0, farRl.withSuffix("/side_tinted"));
        TextureMapping farIdleTexMap = inertIdleTexMap
                .copyAndUpdate(KlaxonTextureSlots.TOP_LAYER_0, farRl.withSuffix("/top_tinted"))
                .put(KlaxonTextureSlots.SIDE_LAYER_0, farRl.withSuffix("/side_tinted"));
        TextureMapping closeTriggeredTexMap = inertTriggeredTexMap
                .copyAndUpdate(KlaxonTextureSlots.TOP_LAYER_0, closeRl.withSuffix("/top_tinted"))
                .put(KlaxonTextureSlots.SIDE_LAYER_0, closeRl.withSuffix("/side_tinted"));
        TextureMapping closeIdleTexMap = inertIdleTexMap
                .copyAndUpdate(KlaxonTextureSlots.TOP_LAYER_0, closeRl.withSuffix("/top_tinted"))
                .put(KlaxonTextureSlots.SIDE_LAYER_0, closeRl.withSuffix("/side_tinted"));
        TextureMapping imminentTriggeredTexMap = inertTriggeredTexMap
                .copyAndUpdate(KlaxonTextureSlots.TOP_LAYER_0, imminentRl.withSuffix("/top_tinted"))
                .put(KlaxonTextureSlots.SIDE_LAYER_0, imminentRl.withSuffix("/side_tinted"));
        TextureMapping imminentIdleTexMap = inertIdleTexMap
                .copyAndUpdate(KlaxonTextureSlots.TOP_LAYER_0, imminentRl.withSuffix("/top_tinted"))
                .put(KlaxonTextureSlots.SIDE_LAYER_0, imminentRl.withSuffix("/side_tinted"));

        ResourceLocation inertTriggeredModelRl = KlaxonModelTemplates.CUBE_TOP_SIDE_BOTTOM_TWO_LAYERS.createWithSuffix(block, "/inert_triggered", inertTriggeredTexMap, this.generator.modelOutput);
        ResourceLocation inertIdleModelRl = KlaxonModelTemplates.CUBE_TOP_SIDE_BOTTOM_TWO_LAYERS.createWithSuffix(block, "/inert_idle", inertIdleTexMap, this.generator.modelOutput);
        ResourceLocation farTriggeredModelRl = KlaxonModelTemplates.CUBE_TOP_SIDE_BOTTOM_TWO_LAYERS.createWithSuffix(block, "/far_triggered", farTriggeredTexMap, this.generator.modelOutput);
        ResourceLocation farIdleModelRl = KlaxonModelTemplates.CUBE_TOP_SIDE_BOTTOM_TWO_LAYERS.createWithSuffix(block, "/far_idle", farIdleTexMap, this.generator.modelOutput);
        ResourceLocation closeTriggeredModelRl = KlaxonModelTemplates.CUBE_TOP_SIDE_BOTTOM_TWO_LAYERS.createWithSuffix(block,"/close_triggered", closeTriggeredTexMap, this.generator.modelOutput);
        ResourceLocation closeIdleModelRl = KlaxonModelTemplates.CUBE_TOP_SIDE_BOTTOM_TWO_LAYERS.createWithSuffix(block,"/close_idle", closeIdleTexMap, this.generator.modelOutput);
        ResourceLocation imminentTriggeredModelRl = KlaxonModelTemplates.CUBE_TOP_SIDE_BOTTOM_TWO_LAYERS.createWithSuffix(block,"/imminent_triggered", imminentTriggeredTexMap, this.generator.modelOutput);
        ResourceLocation imminentIdleModelRl = KlaxonModelTemplates.CUBE_TOP_SIDE_BOTTOM_TWO_LAYERS.createWithSuffix(block,"/imminent_idle", imminentIdleTexMap, this.generator.modelOutput);

        generator.delegateItemModel(block, inertIdleModelRl);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.properties(ModularExplosiveBlock.FUSE, ModularExplosiveBlock.TRIGGERED)
                        .select(FuseState.INERT, true, modelVariant(inertTriggeredModelRl))
                        .select(FuseState.INERT, false, modelVariant(inertIdleModelRl))
                        .select(FuseState.FAR, true, modelVariant(farTriggeredModelRl))
                        .select(FuseState.FAR, false, modelVariant(farIdleModelRl))
                        .select(FuseState.CLOSE, true, modelVariant(closeTriggeredModelRl))
                        .select(FuseState.CLOSE, false, modelVariant(closeIdleModelRl))
                        .select(FuseState.IMMINENT, true, modelVariant(imminentTriggeredModelRl))
                        .select(FuseState.IMMINENT, false, modelVariant(imminentIdleModelRl))
                )
        );
    }

    protected void registerDeepslateBlastProcessor(Block block) {

        TextureMapping baseUnlooted = new TextureMapping()
                .put(TextureSlot.BOTTOM, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_BOTTOM)
                .put(TextureSlot.SIDE, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_SIDE_UNCERTAIN)
                .put(TextureSlot.TOP, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_TOP_UNCERTAIN)
                .put(TextureSlot.BACK, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_BACK)
                .put(TextureSlot.PARTICLE, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_BOTTOM);
        TextureMapping baseFull = baseUnlooted
                .copyAndUpdate(TextureSlot.SIDE, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_SIDE_CLOSED)
                .put(TextureSlot.TOP, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_TOP_CLOSED);
        TextureMapping baseIngredientOnly = baseFull.copyAndUpdate(TextureSlot.SIDE, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_SIDE_OPEN);
        TextureMapping baseCatalystOnly = baseFull.copyAndUpdate(TextureSlot.TOP, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_TOP_OPEN);
        TextureMapping baseEmpty = baseCatalystOnly.copyAndUpdate(TextureSlot.SIDE, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_SIDE_OPEN);

        ResourceLocation unlootedLit = KlaxonModelTemplates.DEEPSLATE_BLAST_PROCESSOR.createWithSuffix(
                block,
                "/unlooted_lit",
                baseUnlooted.copyAndUpdate(TextureSlot.FRONT, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_FRONT_LIT),
                generator.modelOutput
        );
        ResourceLocation unlootedUnlit = KlaxonModelTemplates.DEEPSLATE_BLAST_PROCESSOR.createWithSuffix(
                block,
                "/unlooted_unlit",
                baseUnlooted.copyAndUpdate(TextureSlot.FRONT, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_FRONT_UNLIT),
                generator.modelOutput
        );
        ResourceLocation fullLit = KlaxonModelTemplates.DEEPSLATE_BLAST_PROCESSOR.createWithSuffix(
                block,
                "/full_lit",
                baseFull.copyAndUpdate(TextureSlot.FRONT, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_FRONT_LIT),
                generator.modelOutput
        );
        ResourceLocation fullUnlit = KlaxonModelTemplates.DEEPSLATE_BLAST_PROCESSOR.createWithSuffix(
                block,
                "/full_unlit",
                baseFull.copyAndUpdate(TextureSlot.FRONT, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_FRONT_UNLIT),
                generator.modelOutput
        );
        ResourceLocation ingredientOnlyLit = KlaxonModelTemplates.DEEPSLATE_BLAST_PROCESSOR.createWithSuffix(
                block,
                "/ingredient_only_lit",
                baseIngredientOnly.copyAndUpdate(TextureSlot.FRONT, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_FRONT_LIT),
                generator.modelOutput
        );
        ResourceLocation ingredientOnlyUnlit = KlaxonModelTemplates.DEEPSLATE_BLAST_PROCESSOR.createWithSuffix(
                block,
                "/ingredient_only_unlit",
                baseIngredientOnly.copyAndUpdate(TextureSlot.FRONT, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_FRONT_UNLIT),
                generator.modelOutput
        );
        ResourceLocation catalystOnlyLit = KlaxonModelTemplates.DEEPSLATE_BLAST_PROCESSOR.createWithSuffix(
                block,
                "/catalyst_only_lit",
                baseCatalystOnly.copyAndUpdate(TextureSlot.FRONT, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_FRONT_LIT),
                generator.modelOutput
        );
        ResourceLocation catalystOnlyUnlit = KlaxonModelTemplates.DEEPSLATE_BLAST_PROCESSOR.createWithSuffix(
                block,
                "/catalyst_only_unlit",
                baseCatalystOnly.copyAndUpdate(TextureSlot.FRONT, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_FRONT_UNLIT),
                generator.modelOutput
        );
        ResourceLocation emptyLit = KlaxonModelTemplates.DEEPSLATE_BLAST_PROCESSOR.createWithSuffix(
                block,
                "/empty_lit",
                baseEmpty.copyAndUpdate(TextureSlot.FRONT, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_FRONT_LIT),
                generator.modelOutput
        );
        ResourceLocation emptyUnlit = KlaxonModelTemplates.DEEPSLATE_BLAST_PROCESSOR.createWithSuffix(
                block,
                "/empty_unlit",
                baseEmpty.copyAndUpdate(TextureSlot.FRONT, KlaxonTextures.DEEPSLATE_BLAST_PROCESSOR_FRONT_UNLIT),
                generator.modelOutput
        );

        generator.delegateItemModel(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.value(), emptyUnlit);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.value())
                .with(PropertyDispatch.properties(KlaxonBlockStateProperties.DEEPSLATE_BLAST_PROCESSOR_LOOT_STATE, BlockStateProperties.LIT)
                        .select(DeepslateBlastProcessorLootState.UNLOOTED, true, modelVariant(unlootedLit))
                        .select(DeepslateBlastProcessorLootState.UNLOOTED, false, modelVariant(unlootedUnlit))
                        .select(DeepslateBlastProcessorLootState.FULL, true, modelVariant(fullLit))
                        .select(DeepslateBlastProcessorLootState.FULL, false, modelVariant(fullUnlit))
                        .select(DeepslateBlastProcessorLootState.INGREDIENT_ONLY, true, modelVariant(ingredientOnlyLit))
                        .select(DeepslateBlastProcessorLootState.INGREDIENT_ONLY, false, modelVariant(ingredientOnlyUnlit))
                        .select(DeepslateBlastProcessorLootState.CATALYST_ONLY, true, modelVariant(catalystOnlyLit))
                        .select(DeepslateBlastProcessorLootState.CATALYST_ONLY, false, modelVariant(catalystOnlyUnlit))
                        .select(DeepslateBlastProcessorLootState.EMPTY, true, modelVariant(emptyLit))
                        .select(DeepslateBlastProcessorLootState.EMPTY, false, modelVariant(emptyUnlit))
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
                KlaxonTextureSlots.U_BEND_CURVE, positiveUBendId,
                KlaxonTextureSlots.U_BEND_BOTTOM, bottomId,
                KlaxonTextureSlots.U_BEND_SIDE, positiveSideId,
                TextureSlot.PARTICLE, positiveSideId
        );
        Map<TextureSlot, ResourceLocation> negativeTextureMap = Map.of(
                TextureSlot.BOTTOM, topId,
                KlaxonTextureSlots.U_BEND_CURVE, negativeUBendId,
                KlaxonTextureSlots.U_BEND_BOTTOM, bottomId,
                KlaxonTextureSlots.U_BEND_SIDE, negativeSideId,
                TextureSlot.PARTICLE, negativeSideId
        );

        // create both x and z variants
        generator.modelOutput.accept(
                xAxisPositiveModelIdentifier,
                () -> KlaxonModelTemplates.PIPE_MATRIX_U_BEND_X_POSITIVE.createBaseTemplate(xAxisPositiveModelIdentifier, positiveTextureMap)
        );
        generator.modelOutput.accept(
                zAxisPositiveModelIdentifier,
                () -> KlaxonModelTemplates.PIPE_MATRIX_U_BEND_Z_POSITIVE.createBaseTemplate(zAxisPositiveModelIdentifier, positiveTextureMap)
        );
        generator.modelOutput.accept(
                xAxisNegativeModelIdentifier,
                () -> KlaxonModelTemplates.PIPE_MATRIX_U_BEND_X_NEGATIVE.createBaseTemplate(xAxisNegativeModelIdentifier, negativeTextureMap)
        );
        generator.modelOutput.accept(
                zAxisNegativeModelIdentifier,
                () -> KlaxonModelTemplates.PIPE_MATRIX_U_BEND_Z_NEGATIVE.createBaseTemplate(zAxisNegativeModelIdentifier, negativeTextureMap)
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
                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
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
                KlaxonTextureSlots.CASING, blockId.withPath((path) -> "block/" + path + "/casing"),
                KlaxonTextureSlots.CORE, blockId.withPath((path) -> "block/" + path + "/core"),
                TextureSlot.PARTICLE, blockId.withPath((path) -> "block/" + path + "/casing")
        );

        generator.modelOutput.accept(
                normalModelIdentifier,
                () -> KlaxonModelTemplates.NORMAL_NETHER_REACTOR_CORE.createBaseTemplate(
                        normalModelIdentifier,
                        textureMap
                )
        );
        generator.modelOutput.accept(
                rotatedModelIdentifier,
                () -> KlaxonModelTemplates.ROTATED_NETHER_REACTOR_CORE.createBaseTemplate(
                        rotatedModelIdentifier,
                        textureMap
                )
        );

        generator.delegateItemModel(block, normalModelIdentifier);
    }

    protected void registerPressurePlate(Block pressurePlateBlock, Block materialBlock) {
        TextureMapping mapping = TextureMapping.defaultTexture(materialBlock);
        ResourceLocation upRl = ModelTemplates.PRESSURE_PLATE_UP.create(pressurePlateBlock, mapping, generator.modelOutput);
        ResourceLocation downRl = ModelTemplates.PRESSURE_PLATE_DOWN.create(pressurePlateBlock, mapping, generator.modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createPressurePlate(pressurePlateBlock, upRl, downRl));
    }

    protected void registerFaultyHeavyGatedPressurePlate(Block pressurePlateBlock, Block materialBlock) {
        TextureMapping mapping = TextureMapping.defaultTexture(materialBlock);
        ResourceLocation upRl = ModelTemplates.PRESSURE_PLATE_UP.create(pressurePlateBlock, mapping, generator.modelOutput);
        ResourceLocation downRl = ModelTemplates.PRESSURE_PLATE_DOWN.create(pressurePlateBlock, mapping, generator.modelOutput);
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(pressurePlateBlock)
                .with(PropertyDispatch.property(FaultyHeavyGatedPressurePlateBlock.STATE)
                        .select(FaultyHeavyGatedPressurePlateBlock.State.UNPRESSED, modelVariant(upRl))
                        .select(FaultyHeavyGatedPressurePlateBlock.State.PRESSED, modelVariant(downRl))
                        .select(FaultyHeavyGatedPressurePlateBlock.State.STRESSED, modelVariant(downRl))
                )
        );
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

    private static Variant modelVariant(Holder<Block> holder, String suffix) {
        return modelVariant(holder.value(), suffix);
    }

    private static Variant modelVariant(Block block, String suffix) {
        return modelVariant(getNestedBlockSubModelId(block, suffix));
    }

    private static Variant modelVariant(ResourceLocation modelId) {
        return Variant.variant().with(VariantProperties.MODEL, modelId);
    }
}
