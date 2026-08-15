package net.myriantics.klaxon.mechanics.turbine_generator.power_source;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.myriantics.klaxon.block.machines.energy.generators.turbine.TurbineGeneratorBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class StaticTurbineGeneratorPowerSource {

    public static final Codec<StaticTurbineGeneratorPowerSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SideChecker.CODEC.fieldOf("side_checker").forGetter(i -> i.checker),
            Codec.intRange(0, TurbineGeneratorBlockEntity.MAX_POWER_SOURCE_RANGE).optionalFieldOf("max_range", 0).forGetter(i -> i.range),
            Codec.LONG.fieldOf("target_velocity").validate(aLong -> aLong > 0 ? DataResult.success(aLong) : DataResult.error(() -> "Target velocity must be greater than 0")).forGetter(i -> i.targetVelocity)
    ).apply(instance, StaticTurbineGeneratorPowerSource::new));

    private final SideChecker checker;
    private final int range;
    private final long targetVelocity;

    private StaticTurbineGeneratorPowerSource(SideChecker checker, int range, long targetVelocity) {
        this.checker = checker;
        this.range = range;
        this.targetVelocity = targetVelocity;
    }

    public boolean isWithinRange(int offset) {
        return offset < this.range;
    }

    public long getTargetVelocity() {
        return this.targetVelocity;
    }

    public boolean test(BlockInWorld blockInWorld, Direction generatorFacing) {
        return this.checker.valid(blockInWorld, generatorFacing);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int range = 1;
        private long targetVelocity = -1;
        private SideChecker checker = null;

        public Builder range(int range) {
            if (range < 1 || range > TurbineGeneratorBlockEntity.MAX_POWER_SOURCE_RANGE) {
                throw new UnsupportedOperationException("Range must be between 1 & 32!");
            }
            this.range = range;
            return this;
        }

        public Builder targetVelocity(long targetVelocity) {
            this.targetVelocity = targetVelocity;
            return this;
        }

        public Builder nonSided(BlockPredicate predicate) {
            this.checker = new AnySideChecker(predicate);
            return this;
        }

        public Builder sided(Map<Direction, BlockPredicate> map) {
            this.checker = new RestrictedSideChecker(map);
            return this;
        }

        public Builder requireGeneratorUpwards(BlockPredicate predicate) {
            this.checker = new RestrictedSideChecker(Map.of(Direction.UP, predicate));
            return this;
        }

        public Builder requireGeneratorDownwards(BlockPredicate predicate) {
            this.checker = new RestrictedSideChecker(Map.of(Direction.DOWN, predicate));
            return this;
        }

        public StaticTurbineGeneratorPowerSource build() {
            if (this.checker == null) {
                throw new UnsupportedOperationException("Cannot instantiate a StaticTurbineGeneratorPowerSource without a SideChecker!");
            }

            if (this.targetVelocity < 0) {
                throw new UnsupportedOperationException("Cannot instantiate a StaticTurbineGeneratorPowerSource without defining a target velocity!");
            }

            return new StaticTurbineGeneratorPowerSource(this.checker, this.range, this.targetVelocity);
        }
    }

    private interface SideChecker {
        Codec<SideChecker> CODEC = Codec.xor(AnySideChecker.CODEC, RestrictedSideChecker.CODEC).xmap(
                either -> either.map(anySideChecker -> anySideChecker, restrictedSideChecker -> restrictedSideChecker),
                sideChecker -> {
                    if (sideChecker instanceof AnySideChecker anySideChecker) {
                        return Either.left(anySideChecker);
                    } else if (sideChecker instanceof RestrictedSideChecker restrictedSideChecker) {
                        return Either.right(restrictedSideChecker);
                    } else {
                        throw new UnsupportedOperationException("Side checker is neither [Restricted] nor [Any]");
                    }
                }
        );

        boolean valid(BlockInWorld block, Direction generatorFacing);
    }

    private static final class AnySideChecker implements SideChecker {

        public static final Codec<AnySideChecker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPredicate.CODEC.fieldOf("non_sided").forGetter(i -> i.predicate)
        ).apply(instance, AnySideChecker::new));

        private final BlockPredicate predicate;

        private AnySideChecker(BlockPredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean valid(BlockInWorld block, Direction generatorFacing) {
            return predicate.matches(block);
        }
    }

    private static final class RestrictedSideChecker implements SideChecker {

        public static final Codec<RestrictedSideChecker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.simpleMap(Direction.CODEC, BlockPredicate.CODEC, StringRepresentable.keys(Direction.values())).fieldOf("sided").forGetter(i -> i.generatorFacing2BlockPredicate)
        ).apply(instance, RestrictedSideChecker::new));

        private final Map<Direction, BlockPredicate> generatorFacing2BlockPredicate;

        private RestrictedSideChecker(Map<Direction, BlockPredicate> generatorFacing2BlockPredicate) {
            this.generatorFacing2BlockPredicate = generatorFacing2BlockPredicate;
        }

        @Override
        public boolean valid(BlockInWorld block, Direction generatorFacing) {
            @Nullable BlockPredicate predicate = this.generatorFacing2BlockPredicate.get(generatorFacing);
            return predicate != null && predicate.matches(block);
        }
    }
}
