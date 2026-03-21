package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluids;

public class HorizontalHallnoxGrowthFeature extends Feature<HorizontalHallnoxGrowthFeatureConfig> {
    public HorizontalHallnoxGrowthFeature(Codec<HorizontalHallnoxGrowthFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<HorizontalHallnoxGrowthFeatureConfig> context) {
        WorldGenLevel structureWorldAccess = context.level();
        HorizontalHallnoxGrowthFeatureConfig config = context.config();
        BlockState stemBlock = config.stemBlock();
        BlockState wartBlock = config.wartBlock();
        BlockState podBlock = config.podBlock();
        BlockPredicate replaceableBlocks = config.replaceableBlocks();
        BlockPredicate featureUsedBlocks = BlockPredicate.matchesBlocks(stemBlock.getBlock(), wartBlock.getBlock(), podBlock.getBlock());
        BlockPos originPos = context.origin();
        RandomSource random = context.random();
        Direction facing = config.horizontalFacing();
        int maxLength = 5;

        // make sure we can actually spawn stuff - cap height if not
        for (int horizDiff = 0; horizDiff < maxLength - 1; horizDiff++) {
            if (!isReplaceable(structureWorldAccess, originPos.relative(facing, horizDiff), replaceableBlocks, featureUsedBlocks)) {
                // don't even try to place such a small growth
                if (horizDiff < 3) return false;
                maxLength = horizDiff + 1;
                break;
            }
        }

        BlockPos stemEndPos = generateStem(structureWorldAccess, originPos, random, maxLength, config, replaceableBlocks, featureUsedBlocks, facing);
        generateCap(structureWorldAccess, stemEndPos, random, maxLength, config, replaceableBlocks, featureUsedBlocks, facing);

        return true;
    }

    // returns ending block of stem
    private BlockPos generateStem(WorldGenLevel world, BlockPos originPos, RandomSource random, int maxLength, HorizontalHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks, Direction facing) {
        BlockPos.MutableBlockPos workingPos = new BlockPos.MutableBlockPos().set(originPos);
        BlockState stemState = config.stemBlock();
        if (stemState.hasProperty(BlockStateProperties.AXIS)) {
            stemState = stemState.setValue(BlockStateProperties.AXIS, facing.getAxis());
        }

        // place block below stem if possible & if stem is long enough
        if (maxLength > 2) setBlockStateIfPossible(world, originPos.below(), stemState, replaceableBlocks, featureUsedBlocks);

        // place main stem segment - goes to 1 less than max length
        for (int horizDiff = 0; horizDiff < maxLength - 1; horizDiff++) {
            workingPos = originPos.relative(facing, horizDiff).mutable();
            // ramps up height of generation as you go
            workingPos.setY(originPos.getY() + horizDiff / (maxLength / 2));
            setBlockStateIfPossible(world, workingPos, stemState, replaceableBlocks, featureUsedBlocks);
        }

        return workingPos.immutable();
    }

    // returns last block generated in center line of cap
    private BlockPos generateCap(WorldGenLevel world, BlockPos stemFinishPos, RandomSource random, int maxLength, HorizontalHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks, Direction facing) {
        BlockPos startingPos = stemFinishPos.relative(facing);
        BlockPos.MutableBlockPos workingPos = new BlockPos.MutableBlockPos().set(startingPos);
        BlockState wartState = config.wartBlock();

        Direction.Axis perpendicularAxis = facing.getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

        for (int horizDiff = 0; horizDiff < maxLength + 1 ; horizDiff++) {
            workingPos = startingPos.relative(facing.getOpposite(), horizDiff).mutable();
            workingPos.setY(startingPos.getY() + horizDiff / (maxLength / 2));

            // place center line of growth and accent line above it - break if spine fails to place
            if (!setBlockStateIfPossible(world, workingPos, wartState, replaceableBlocks, featureUsedBlocks)) break;
            setBlockStateIfPossible(world, workingPos.above(), wartState, replaceableBlocks, featureUsedBlocks);

            // place blocks to the side of the
            for (Direction.AxisDirection direction : Direction.AxisDirection.values()) {
                Direction offsetDir = Direction.fromAxisAndDirection(perpendicularAxis, direction);
                int radius = maxLength / 2;
                for (int i = 0; i < radius; i++) {
                    BlockPos targetPos = workingPos.relative(offsetDir, i + 1);
                    // wack function to make stuff deteriorate the farther it is from the origin point
                    if (i == 0 || random.nextFloat() < (0.8 - (0.05 * horizDiff) - (0.1 * i))) {
                        if (setBlockStateIfPossible(world, targetPos, wartState, replaceableBlocks, featureUsedBlocks) && i == radius - 1) {
                            generateDroop(world, targetPos.below(), random, maxLength, config, replaceableBlocks, featureUsedBlocks, facing);
                        }
                    }
                }
            }
        }

        return workingPos;
    }

    private void generateDroop(WorldGenLevel world, BlockPos originPos, RandomSource random, int maxLength, HorizontalHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks, Direction facing) {
        BlockState wartState = config.wartBlock();
        BlockState podState = config.podBlock();
        if (podState.hasProperty(BlockStateProperties.FACING)) {
            podState = podState.setValue(BlockStateProperties.FACING, Direction.UP);
        }

        for (int i = 0; i < maxLength / 2; i++) {
            if (random.nextFloat() > 0.3f * i) {
                // try to place growth - if this placement rolls to succeed but can't replace block, break out of loop.
                if (random.nextFloat() < 0.8) if (!setBlockStateIfPossible(world, originPos.below(i), wartState, replaceableBlocks, featureUsedBlocks)) break;
            } else {
                // i love rng. place pod and supporting block
                if (random.nextFloat() < 0.8) {
                    setBlockStateIfPossible(world, originPos.below(i - 1), wartState, replaceableBlocks, featureUsedBlocks);
                    setBlockStateIfPossible(world, originPos.below(i), podState, replaceableBlocks, featureUsedBlocks);
                }
                break;
            }
        }
    }

    private static boolean isReplaceable(WorldGenLevel world, BlockPos pos, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks) {
        if (world.isStateAtPosition(pos, BlockBehaviour.BlockStateBase::canBeReplaced)) {
            return true;
        } else {
            return replaceableBlocks.or(featureUsedBlocks).test(world, pos);
        }
    }

    // returns true if operation was successful - false if it wasn't
    private boolean setBlockStateIfPossible(WorldGenLevel world, BlockPos pos, BlockState state, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks) {
        // preserve waterlogged status if possible
        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && world.getFluidState(pos).is(Fluids.WATER)) {
            state = state.setValue(BlockStateProperties.WATERLOGGED, true);
        }

        if (isReplaceable(world, pos, replaceableBlocks, featureUsedBlocks)) {
            setBlock(world, pos, state);
            return true;
        } else {
            return false;
        }
    }
}
