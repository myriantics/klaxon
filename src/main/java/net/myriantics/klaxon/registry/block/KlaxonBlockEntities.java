package net.myriantics.klaxon.registry.block;

import net.minecraft.core.BlockBox;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;

public abstract class KlaxonBlockEntities {
    public static final Holder<BlockEntityType<DeepslateBlastProcessorBlockEntity>> DEEPSLATE_BLAST_PROCESSOR_BLOCK_ENTITY = register(
            "deepslate_blast_processor",
            KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR,
            DeepslateBlastProcessorBlockEntity::new
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Block Entities!");
        addSupporting(BlockEntityType.SIGN , KlaxonBlocks.HALLNOX_SIGN);
        addSupporting(BlockEntityType.SIGN , KlaxonBlocks.HALLNOX_WALL_SIGN);
        addSupporting(BlockEntityType.HANGING_SIGN, KlaxonBlocks.HALLNOX_HANGING_SIGN);
        addSupporting(BlockEntityType.HANGING_SIGN, KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN);
    }

    private static void addSupporting(BlockEntityType<?> type, Holder<Block> holder) {
        addSupporting(type, holder.value());
    }

    private static void addSupporting(BlockEntityType<?> type, Block block) {
        type.addSupportedBlock(block);
    }

    public static <T extends BlockEntity> Holder<BlockEntityType<T>> register(String id, Holder<Block> holder, BlockEntityType.BlockEntitySupplier<T> factory) {
        return register(id, holder.value(), factory);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> Holder<BlockEntityType<T>> register(String id, Block block, BlockEntityType.BlockEntitySupplier<T> factory) {
        return (Holder<BlockEntityType<T>>) (Object) Registry.registerForHolder(BuiltInRegistries.BLOCK_ENTITY_TYPE, KlaxonCommon.locate(id), BlockEntityType.Builder.of(factory, block).build());
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, KlaxonCommon.locate(id), builder.build());
    }
}
