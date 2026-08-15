package net.myriantics.klaxon.datagen.custom.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.mechanics.turbine_generator.power_source.StaticTurbineGeneratorPowerSource;
import net.myriantics.klaxon.registry.behavior.KlaxonTurbineGeneratorPowerSources;

import java.util.Map;

public class KlaxonTurbineGeneratorPowerSourceProvider extends KlaxonDynamicRegistrySubProvider<StaticTurbineGeneratorPowerSource> {
    public KlaxonTurbineGeneratorPowerSourceProvider(HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {
        add(
                KlaxonTurbineGeneratorPowerSources.CAMPFIRE,
                StaticTurbineGeneratorPowerSource.builder()
                        .range(5)
                        .targetVelocity(100)
                        .requireGeneratorDownwards(
                                campfire(Blocks.CAMPFIRE, false)
                        )
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.SIGNAL_CAMPFIRE,
                StaticTurbineGeneratorPowerSource.builder()
                        .range(10)
                        .targetVelocity(200)
                        .requireGeneratorDownwards(
                                campfire(Blocks.CAMPFIRE, true)
                        )
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.UP_BUBBLE_COLUMN,
                StaticTurbineGeneratorPowerSource.builder()
                        .targetVelocity(200)
                        .requireGeneratorDownwards(
                                BlockPredicate.Builder.block()
                                        .of(Blocks.BUBBLE_COLUMN)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BubbleColumnBlock.DRAG_DOWN, false))
                                        .build()
                        )
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.DOWN_BUBBLE_COLUMN,
                StaticTurbineGeneratorPowerSource.builder()
                        .targetVelocity(200)
                        .requireGeneratorUpwards(
                                BlockPredicate.Builder.block()
                                        .of(Blocks.BUBBLE_COLUMN)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BubbleColumnBlock.DRAG_DOWN, true))
                                        .build()
                        ).build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.SCULK_SHRIEKER,
                StaticTurbineGeneratorPowerSource.builder()
                        .targetVelocity(40)
                        .range(3)
                        .requireGeneratorDownwards(
                                BlockPredicate.Builder.block()
                                        .of(Blocks.SCULK_SHRIEKER)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SculkShriekerBlock.SHRIEKING, true))
                                        .build()
                        ).build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.BREWING_STAND,
                StaticTurbineGeneratorPowerSource.builder()
                        .targetVelocity(5)
                        .requireGeneratorDownwards(
                                BlockPredicate.Builder.block()
                                        .of(Blocks.BREWING_STAND)
                                        .build()
                        ).build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.SMOKER,
                StaticTurbineGeneratorPowerSource.builder()
                        .targetVelocity(250)
                        .requireGeneratorDownwards(
                                BlockPredicate.Builder.block()
                                        .of(Blocks.SMOKER)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SmokerBlock.LIT, true))
                                        .build()
                        )
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.FURNACE,
                StaticTurbineGeneratorPowerSource.builder()
                        .targetVelocity(250)
                        .sided(furnace(Blocks.FURNACE))
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.BLAST_FURNACE,
                StaticTurbineGeneratorPowerSource.builder()
                        .targetVelocity(300)
                        .sided(furnace(Blocks.BLAST_FURNACE))
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.VAULT,
                StaticTurbineGeneratorPowerSource.builder()
                        .targetVelocity(45)
                        .range(2)
                        .requireGeneratorDownwards(
                                BlockPredicate.Builder.block()
                                        .of(Blocks.VAULT)
                                        .build()
                        )
                        .build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.TRIAL_SPAWNER_ACTIVE,
                StaticTurbineGeneratorPowerSource.builder()
                        .targetVelocity(90)
                        .range(3)
                        .requireGeneratorDownwards(
                                BlockPredicate.Builder.block()
                                        .of(Blocks.TRIAL_SPAWNER)
                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(TrialSpawnerBlock.STATE, TrialSpawnerState.ACTIVE)
                                        )
                                        .build()
                        ).build()
        );
        add(
                KlaxonTurbineGeneratorPowerSources.TRIAL_SPAWNER_COOLDOWN,
                StaticTurbineGeneratorPowerSource.builder()
                        .targetVelocity(60)
                        .range(3)
                        .requireGeneratorDownwards(
                                BlockPredicate.Builder.block()
                                        .of(Blocks.TRIAL_SPAWNER)
                                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(TrialSpawnerBlock.STATE, TrialSpawnerState.COOLDOWN)
                                        )
                                        .build()
                        )
                        .build()
        );
    }

    protected static Map<Direction, BlockPredicate> furnace(Block... furnaceBlocks) {
        return Map.of(
                Direction.NORTH,
                BlockPredicate.Builder.block()
                        .of(furnaceBlocks)
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(AbstractFurnaceBlock.FACING, Direction.SOUTH)
                                .hasProperty(AbstractFurnaceBlock.LIT, true)
                        )
                        .build(),
                Direction.EAST,
                BlockPredicate.Builder.block()
                        .of(furnaceBlocks)
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(AbstractFurnaceBlock.FACING, Direction.WEST)
                                .hasProperty(AbstractFurnaceBlock.LIT, true)
                        )
                        .build(),
                Direction.SOUTH,
                BlockPredicate.Builder.block()
                        .of(furnaceBlocks)
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(AbstractFurnaceBlock.FACING, Direction.NORTH)
                                .hasProperty(AbstractFurnaceBlock.LIT, true)
                        )
                        .build(),
                Direction.WEST,
                BlockPredicate.Builder.block()
                        .of(furnaceBlocks)
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(AbstractFurnaceBlock.FACING, Direction.EAST)
                                .hasProperty(AbstractFurnaceBlock.LIT, true)
                        )
                        .build()
        );
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
}
