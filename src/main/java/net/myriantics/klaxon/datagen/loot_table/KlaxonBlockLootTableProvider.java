package net.myriantics.klaxon.datagen.loot_table;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.myriantics.klaxon.block.functional.pressure_plate.FaultyHeavyGatedPressurePlateBlock;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class KlaxonBlockLootTableProvider extends FabricBlockLootTableProvider {

    public KlaxonBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // machines
        dropSelf(KlaxonBlocks.STEEL_BLAST_PROCESSOR);
        dropSelf(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
        dropSelf(KlaxonBlocks.PRECISION_DISPENSER);
        dropSelf(KlaxonBlocks.NETHER_REACTOR_CORE);
        dropSelf(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE);
        dropSelf(KlaxonBlocks.STEEL_WORKBENCH);
        add(KlaxonBlocks.MODULAR_EXPLOSIVE_BLOCK, this::createModularExplosiveBlock);
        dropSelf(KlaxonBlocks.FILING_CABINET_BASE);

        // steel
        dropSelf(KlaxonBlocks.STEEL_BLOCK);
        dropSelf(KlaxonBlocks.STEEL_CASING);
        dropSelf(KlaxonBlocks.STEEL_PLATING_BLOCK);
        dropSelf(KlaxonBlocks.STEEL_WIRE_SPOOL_BLOCK);
        add(KlaxonBlocks.STEEL_DOOR, this::createDoorTable);
        dropSelf(KlaxonBlocks.STEEL_TRAPDOOR);
        dropSelf(KlaxonBlocks.HEAVY_GATED_PRESSURE_PLATE);

        // crude steel
        dropSelf(KlaxonBlocks.CRUDE_STEEL_BLOCK);
        dropSelf(KlaxonBlocks.CRUDE_STEEL_CASING);
        dropSelf(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK);
        add(KlaxonBlocks.CRUDE_STEEL_DOOR, this::createDoorTable);
        dropSelf(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR);
        faultyHeavyGatedPressurePlate(KlaxonBlocks.FAULTY_HEAVY_GATED_PRESSURE_PLATE.value());

        // iron
        dropSelf(KlaxonBlocks.IRON_PLATING_BLOCK);
        dropSelf(KlaxonBlocks.IRON_WIRE_SPOOL_BLOCK);

        // gold
        dropSelf(KlaxonBlocks.GOLD_PLATING_BLOCK);
        dropSelf(KlaxonBlocks.GOLD_WIRE_SPOOL_BLOCK);

        // copper
        dropSelf(KlaxonBlocks.COPPER_PLATING_BLOCK);
        dropSelf(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK);
        dropSelf(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK);
        dropSelf(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK);
        dropSelf(KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK);
        dropSelf(KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK);
        dropSelf(KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK);
        dropSelf(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);
        dropSelf(KlaxonBlocks.COPPER_WIRE_SPOOL_BLOCK);
        dropSelf(KlaxonBlocks.EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
        dropSelf(KlaxonBlocks.WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
        dropSelf(KlaxonBlocks.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);
        dropSelf(KlaxonBlocks.WAXED_COPPER_WIRE_SPOOL_BLOCK);
        dropSelf(KlaxonBlocks.WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
        dropSelf(KlaxonBlocks.WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
        dropSelf(KlaxonBlocks.WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);
        dropSelf(KlaxonBlocks.COPPER_PIPE_MATRIX_U_BEND);
        dropSelf(KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_U_BEND);
        dropSelf(KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_U_BEND);
        dropSelf(KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_U_BEND);
        dropSelf(KlaxonBlocks.COPPER_PIPE_MATRIX_SEGMENT);
        dropSelf(KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_SEGMENT);
        dropSelf(KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_SEGMENT);
        dropSelf(KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT);
        dropSelf(KlaxonBlocks.WAXED_COPPER_PIPE_MATRIX_U_BEND);
        dropSelf(KlaxonBlocks.WAXED_EXPOSED_COPPER_PIPE_MATRIX_U_BEND);
        dropSelf(KlaxonBlocks.WAXED_WEATHERED_COPPER_PIPE_MATRIX_U_BEND);
        dropSelf(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PIPE_MATRIX_U_BEND);
        dropSelf(KlaxonBlocks.WAXED_COPPER_PIPE_MATRIX_SEGMENT);
        dropSelf(KlaxonBlocks.WAXED_EXPOSED_COPPER_PIPE_MATRIX_SEGMENT);
        dropSelf(KlaxonBlocks.WAXED_WEATHERED_COPPER_PIPE_MATRIX_SEGMENT);
        dropSelf(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT);

        // rubber
        dropSelf(KlaxonBlocks.RUBBER_BLOCK);
        dropSelf(KlaxonBlocks.RUBBER_SHEET_BLOCK);

        // molten rubber
        dropSelf(KlaxonBlocks.MOLTEN_RUBBER_BLOCK);

        // hallnox
        dropSelf(KlaxonBlocks.HALLNOX_STEM);
        dropSelf(KlaxonBlocks.HALLNOX_HYPHAE);
        dropSelf(KlaxonBlocks.STRIPPED_HALLNOX_STEM);
        dropSelf(KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE);
        dropSelf(KlaxonBlocks.HALLNOX_PLANKS);
        dropSelf(KlaxonBlocks.HALLNOX_STAIRS);
        dropSelf(KlaxonBlocks.HALLNOX_SLAB);
        dropSelf(KlaxonBlocks.HALLNOX_FENCE);
        dropSelf(KlaxonBlocks.HALLNOX_FENCE_GATE);
        add(KlaxonBlocks.HALLNOX_DOOR, this::createDoorTable);
        dropSelf(KlaxonBlocks.HALLNOX_TRAPDOOR);
        dropSelf(KlaxonBlocks.HALLNOX_PRESSURE_PLATE);
        dropSelf(KlaxonBlocks.HALLNOX_BUTTON);
        dropSelf(KlaxonBlocks.HALLNOX_POD);
        dropPottedContents(KlaxonBlocks.POTTED_HALLNOX_POD);
        dropSelf(KlaxonBlocks.HALLNOX_WART_BLOCK);
        dropSelf(KlaxonBlocks.HALLNOX_SIGN);
        dropSelf(KlaxonBlocks.HALLNOX_HANGING_SIGN);
        dropSelf(KlaxonBlocks.HALLNOX_BULB);
    }

    private void dropPottedContents(Holder<Block> block) {
        dropPottedContents(block.value());
    }

    private void add(Holder<Block> holder, Function<Block, LootTable.Builder> function) {
        add(holder.value(), function);
    }

    private void add(Holder<Block> holder, LootTable.Builder builder) {
        add(holder.value(), builder);
    }

    private void dropSelf(Holder<Block> holder) {
        dropSelf(holder.value());
    }

    private void faultyHeavyGatedPressurePlate(Block block) {
        this.add(
                block,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0f))
                                        .add(
                                                this.applyExplosionDecay(
                                                        block,
                                                        LootItem.lootTableItem(block)
                                                                .apply(
                                                                        SetItemCountFunction.setCount(ConstantValue.exactly(0.0f))
                                                                                .when(
                                                                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FaultyHeavyGatedPressurePlateBlock.STATE, FaultyHeavyGatedPressurePlateBlock.State.STRESSED))
                                                                                )
                                                                )
                                                )
                                        )
                        )
        );
    }

    private LootTable.Builder createModularExplosiveBlock(Block block) {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0f))
                                .add(
                                        LootItem.lootTableItem(block)
                                                .apply(
                                                        CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                                .include(KlaxonDataComponentTypes.MODULAR_EXPLOSIVE_BLOCK_CONFIG.value())
                                                                .include(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value())
                                                )
                                ).unwrap()
                );
    }
}