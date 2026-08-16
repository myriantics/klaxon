package net.myriantics.klaxon.datagen.custom.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.mechanics.turbine_generator.power_source.StaticTurbineGeneratorPowerSource;
import net.myriantics.klaxon.registry.behavior.KlaxonTurbineGeneratorPowerSources;

import java.util.Map;
import java.util.function.UnaryOperator;

public class KlaxonTurbineGeneratorPowerSourceProvider extends KlaxonDynamicRegistrySubProvider<StaticTurbineGeneratorPowerSource> {
    public KlaxonTurbineGeneratorPowerSourceProvider(HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {
        add(
                KlaxonTurbineGeneratorPowerSources.CAMPFIRE,
                StaticTurbineGeneratorPowerSource.builder(campfire(Blocks.CAMPFIRE, false), 100)
                        .range(5)
                        .requireGeneratorDownwards()
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.SIGNAL_CAMPFIRE,
                StaticTurbineGeneratorPowerSource.builder(campfire(Blocks.CAMPFIRE, true), 200)
                        .range(10)
                        .requireGeneratorDownwards()
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.BUBBLE_COLUMN,
                StaticTurbineGeneratorPowerSource.builder(simple(Blocks.BUBBLE_COLUMN), 200)
                        .requireWhenGeneratorFacing(Direction.DOWN, requireBoolean(BubbleColumnBlock.DRAG_DOWN, false))
                        .requireWhenGeneratorFacing(Direction.UP, requireBoolean(BubbleColumnBlock.DRAG_DOWN, true))
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.SCULK_SHRIEKER,
                StaticTurbineGeneratorPowerSource.builder(
                        BlockPredicate.Builder.block()
                                .of(Blocks.SCULK_SHRIEKER)
                                .setProperties(
                                        StatePropertiesPredicate.Builder.properties().hasProperty(SculkShriekerBlock.SHRIEKING, true)
                                ).build(),
                                40
                        )
                        .range(3)
                        .requireGeneratorDownwards()
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.BREWING_STAND,
                StaticTurbineGeneratorPowerSource.builder(simple(Blocks.BREWING_STAND), 5)
                        .requireGeneratorDownwards()
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.SMOKER,
                StaticTurbineGeneratorPowerSource.builder(
                        BlockPredicate.Builder.block()
                                .of(Blocks.SMOKER)
                                .setProperties(requireBoolean(SmokerBlock.LIT, true))
                                .build(),
                        250
                        )
                        .requireGeneratorDownwards()
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.FURNACE,
                StaticTurbineGeneratorPowerSource.builder(
                        BlockPredicate.Builder.block()
                                .of(Blocks.FURNACE)
                                .setProperties(requireBoolean(FurnaceBlock.LIT, true))
                                .build(),
                                250
                        )
                        .function(KlaxonTurbineGeneratorPowerSourceProvider::horizontalFacingInverted)
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.BLAST_FURNACE,
                StaticTurbineGeneratorPowerSource.builder(
                        BlockPredicate.Builder.block()
                                .of(Blocks.BLAST_FURNACE)
                                .setProperties(requireBoolean(BlastFurnaceBlock.LIT, true))
                                .build(),
                        300
                        )
                        .function(KlaxonTurbineGeneratorPowerSourceProvider::horizontalFacingInverted)
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.VAULT,
                StaticTurbineGeneratorPowerSource.builder(
                        simple(Blocks.VAULT),
                        45
                        )
                        .range(2)
                        .requireGeneratorDownwards()
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.TRIAL_SPAWNER_ACTIVE,
                StaticTurbineGeneratorPowerSource.builder(
                        BlockPredicate.Builder.block()
                                .of(Blocks.TRIAL_SPAWNER)
                                .setProperties(require(TrialSpawnerBlock.STATE, TrialSpawnerState.ACTIVE))
                                .build(),
                                90
                        )
                        .range(3)
                        .requireGeneratorDownwards()
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.TRIAL_SPAWNER_COOLDOWN,
                StaticTurbineGeneratorPowerSource.builder(
                        BlockPredicate.Builder.block()
                                .of(Blocks.TRIAL_SPAWNER)
                                .setProperties(require(TrialSpawnerBlock.STATE, TrialSpawnerState.ACTIVE))
                                .build(),
                        60
                        )
                        .range(3)
                        .requireGeneratorDownwards()
                        .build()
        );
    }

    protected static StatePropertiesPredicate.Builder requireBoolean(BooleanProperty property, boolean value) {
        return StatePropertiesPredicate.Builder.properties().hasProperty(property, value);
    }

    protected static <T extends Comparable<T> & StringRepresentable> StatePropertiesPredicate.Builder require(Property<T> property, T value) {
        return StatePropertiesPredicate.Builder.properties().hasProperty(property, value);
    }

    protected static StaticTurbineGeneratorPowerSource.Builder horizontalFacingInverted(StaticTurbineGeneratorPowerSource.Builder builder) {
        return builder
                .requireWhenGeneratorFacing(Direction.NORTH, require(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH))
                .requireWhenGeneratorFacing(Direction.EAST, require(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST))
                .requireWhenGeneratorFacing(Direction.SOUTH, require(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH))
                .requireWhenGeneratorFacing(Direction.WEST, require(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));
    }

    protected static BlockPredicate bubbleColumn(Direction pullDirection) {
        if (pullDirection.getAxis() != Direction.Axis.Y) {
            throw new IllegalArgumentException("Bubble columns can only pull up or down!");
        }

        return BlockPredicate.Builder.block()
                .of(Blocks.BUBBLE_COLUMN)
                .setProperties(
                        StatePropertiesPredicate.Builder.properties()
                                .hasProperty(BubbleColumnBlock.DRAG_DOWN, pullDirection == Direction.DOWN)
                )
                .build();
    }

    protected static BlockPredicate campfire(Block campfireBlock, boolean signal) {
        return BlockPredicate.Builder.block()
                .of(campfireBlock)
                .setProperties(
                        StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CampfireBlock.LIT, true)
                                .hasProperty(CampfireBlock.SIGNAL_FIRE, signal)
                )
                .build();
    }

    protected static BlockPredicate simple(Block... blocks) {
        return BlockPredicate.Builder.block().of(blocks).build();
    }
}
