package net.myriantics.klaxon.mechanics.turbine_generator.power_source;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.energy.generators.turbine.TurbineGeneratorBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class StaticTurbineGeneratorPowerSource implements TurbineGeneratorPowerSource {

    public static final Codec<StaticTurbineGeneratorPowerSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPredicate.CODEC.fieldOf("block_predicate").validate(StaticTurbineGeneratorPowerSource::validateBlockPredicate).forGetter(i -> i.predicate),
            SidednessChecker.CODEC.optionalFieldOf("sidedness_checker").forGetter(i -> i.checker),
            Codec.intRange(0, TurbineGeneratorBlockEntity.MAX_STATIC_POWER_SOURCE_RANGE).optionalFieldOf("max_range", 0).forGetter(i -> i.range),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("target_velocity").validate(aLong -> aLong > 0 ? DataResult.success(aLong) : DataResult.error(() -> "Target velocity must be greater than 0")).forGetter(i -> i.targetVelocity)
    ).apply(instance, StaticTurbineGeneratorPowerSource::new));

    private static final Pair<StaticTurbineGeneratorPowerSource, Tag> EMPTY_PAIR = Pair.of(null, null);

    private final BlockPredicate predicate;
    private final Optional<SidednessChecker> checker;
    private final int range;
    private final double targetVelocity;

    private StaticTurbineGeneratorPowerSource(BlockPredicate predicate, Optional<SidednessChecker> checker, int range, double targetVelocity) {
        this.predicate = predicate;
        this.checker = checker;
        this.range = range;
        this.targetVelocity = targetVelocity;
    }

    public boolean isWithinRange(int offset) {
        return offset < this.range;
    }

    @Override
    public double getTargetVelocity() {
        return this.targetVelocity;
    }

    public boolean test(BlockInWorld blockInWorld, Direction generatorFacing) {
        return this.predicate.matches(blockInWorld) && (this.checker.isEmpty() || this.checker.get().valid(blockInWorld, generatorFacing));
    }

    public boolean canBeValidForDirection(Direction generatorFacing) {
        return this.checker.isEmpty() || this.checker.get().canPermitDirection(generatorFacing);
    }

    public Tag save(Tag tag, HolderLookup.Provider registries) {
        return CODEC.encode(this, registries.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }

    public static @Nullable StaticTurbineGeneratorPowerSource load(CompoundTag tag, HolderLookup.Provider registries) {
        return CODEC.decode(registries.createSerializationContext(NbtOps.INSTANCE), tag).resultOrPartial(string -> KlaxonCommon.LOGGER.error("Tried to load invalid static turbine power source: '{}'", string)).orElse(EMPTY_PAIR).getFirst();
    }

    public static Builder builder(BlockPredicate predicate, double targetVelocity) {
        return new Builder(predicate, targetVelocity);
    }

    private static DataResult<BlockPredicate> validateBlockPredicate(BlockPredicate predicate) {
        return predicate.blocks().isEmpty() ? DataResult.error(() -> "Cannot load a Static Turbine Generator Power Source without defined blocks!") : DataResult.success(predicate);
    }

    public static class Builder {
        private final BlockPredicate predicate;
        private final double targetVelocity;
        private final HashMap<Direction, StatePropertiesPredicate> checkerMap = new HashMap<>(6);
        private int range = 1;

        public Builder(BlockPredicate predicate, double targetVelocity) {
            this.predicate = validateBlockPredicate(predicate).getOrThrow();
            this.targetVelocity = targetVelocity;
        }

        public Builder range(int range) {
            if (range < 1 || range > TurbineGeneratorBlockEntity.MAX_STATIC_POWER_SOURCE_RANGE) {
                throw new UnsupportedOperationException("Range must be between 1 & 32!");
            }
            this.range = range;
            return this;
        }

        public Builder function(UnaryOperator<Builder> operator) {
            return operator.apply(this);
        }

        public Builder requireGeneratorUpwards() {
            return this.requireGeneratorFacing(Direction.UP);
        }

        public Builder requireWhenGeneratorUpwards(StatePropertiesPredicate.Builder predicate) {
            return this.requireWhenGeneratorFacing(Direction.UP, predicate);
        }

        public Builder requireGeneratorDownwards() {
            return this.requireGeneratorFacing(Direction.DOWN);
        }

        public Builder requireWhenGeneratorDownwards(StatePropertiesPredicate.Builder predicate) {
            return this.requireWhenGeneratorFacing(Direction.DOWN, predicate);
        }

        public Builder requireGeneratorFacing(Direction direction) {
            return this.requireWhenGeneratorFacing(direction, StatePropertiesPredicate.Builder.properties());
        }

        public Builder requireWhenGeneratorFacing(Direction direction, StatePropertiesPredicate.Builder builder) {
            this.checkerMap.put(direction, builder.build().orElseThrow());
            return this;
        }

        public StaticTurbineGeneratorPowerSource build() {
            return new StaticTurbineGeneratorPowerSource(
                    this.predicate,
                    this.checkerMap.isEmpty() ? null : Optional.of(new SidednessChecker(this.checkerMap)),
                    this.range,
                    this.targetVelocity
            );
        }
    }

    private static final class SidednessChecker {
        private static Codec<SidednessChecker> CODEC = Codec.simpleMap(Direction.CODEC, StatePropertiesPredicate.CODEC, StringRepresentable.keys(Direction.values())).xmap(SidednessChecker::new, i -> i.generatorFacing2RequiredProperties).codec();

        private final Map<Direction, StatePropertiesPredicate> generatorFacing2RequiredProperties;

        private SidednessChecker(Map<Direction, StatePropertiesPredicate> generatorFacing2RequiredProperties) {
            this.generatorFacing2RequiredProperties = generatorFacing2RequiredProperties;
        }

        private boolean canPermitDirection(Direction generatorFacing) {
            return this.generatorFacing2RequiredProperties.containsKey(generatorFacing);
        }

        private boolean valid(BlockInWorld block, Direction generatorFacing) {
            @Nullable StatePropertiesPredicate predicate = this.generatorFacing2RequiredProperties.get(generatorFacing);
            return predicate != null && predicate.matches(block.getState());
        };
    }
}
