package net.myriantics.klaxon.registry.block;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.machines.blast_processor.steel.SteelBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.machines.duct.driver.aio.AIODuctDriverBlockEntity;
import net.myriantics.klaxon.block.machines.duct.segment.DuctSegmentBlockEntity;
import net.myriantics.klaxon.block.machines.energy.contact_charger.BaseContactChargerBlockEntity;
import net.myriantics.klaxon.block.machines.modular_explosive.ModularExplosiveBlockEntity;
import net.myriantics.klaxon.block.machines.precision_dispenser.PrecisionDispenserBlockEntity;

public abstract class KlaxonBlockEntityTypes {
    public static final Holder<BlockEntityType<DeepslateBlastProcessorBlockEntity>> DEEPSLATE_BLAST_PROCESSOR = register(
            "deepslate_blast_processor",
            KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR,
            DeepslateBlastProcessorBlockEntity::new
    );
    public static final Holder<BlockEntityType<SteelBlastProcessorBlockEntity>> STEEL_BLAST_PROCESSOR = register(
            "steel_blast_processor",
            KlaxonBlocks.STEEL_BLAST_PROCESSOR,
            SteelBlastProcessorBlockEntity::new
    );
    public static final Holder<BlockEntityType<PrecisionDispenserBlockEntity>> PRECISION_DISPENSER = register(
            "precision_dispenser",
            KlaxonBlocks.PRECISION_DISPENSER,
            PrecisionDispenserBlockEntity::new
    );
    public static final Holder<BlockEntityType<ModularExplosiveBlockEntity>> MODULAR_EXPLOSIVE = register(
            "modular_explosive",
            KlaxonBlocks.MODULAR_EXPLOSIVE_BLOCK,
            ModularExplosiveBlockEntity::new
    );
    public static final Holder<BlockEntityType<AIODuctDriverBlockEntity>> AIO_DUCT_DRIVER = register(
            "all_in_one_duct_driver",
            KlaxonBlocks.AIO_DUCT_DRIVER,
            AIODuctDriverBlockEntity::new
    );
    public static final Holder<BlockEntityType<DuctSegmentBlockEntity>> DUCT_SEGMENT = register(
            "duct_segment",
            KlaxonBlocks.DUCT_SEGMENT,
            DuctSegmentBlockEntity::new
    );
    public static final Holder<BlockEntityType<BaseContactChargerBlockEntity>> CREATIVE_CONTACT_CHARGER = register(
            "creative_contact_charger",
            KlaxonBlocks.CREATIVE_CONTACT_CHARGER,
            BaseContactChargerBlockEntity::new
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
