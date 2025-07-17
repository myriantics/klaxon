package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class HorizontalHallnoxGrowthFeature extends Feature<HorizontalHallnoxGrowthFeatureConfig> {
    public HorizontalHallnoxGrowthFeature(Codec<HorizontalHallnoxGrowthFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<HorizontalHallnoxGrowthFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        HorizontalHallnoxGrowthFeatureConfig config = context.getConfig();
        BlockState stemBlock = config.stemBlock();
        BlockState wartBlock = config.wartBlock();
        BlockState podBlock = config.podBlock();
        BlockPredicate replaceableBlocks = config.replaceableBlocks();
        BlockPredicate featureUsedBlocks = BlockPredicate.matchingBlocks(stemBlock.getBlock(), wartBlock.getBlock(), podBlock.getBlock());
        BlockPos originPos = context.getOrigin();
        Random random = context.getRandom();
        Direction facing = config.horizontalFacing();
        int maxLength = 5;

        // make sure we can actually spawn stuff - cap height if not
        for (int horizDiff = 0; horizDiff < maxLength - 1; horizDiff++) {
            if (!isReplaceable(structureWorldAccess, originPos.offset(facing, horizDiff), replaceableBlocks, featureUsedBlocks)) {
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
    private BlockPos generateStem(StructureWorldAccess world, BlockPos originPos, Random random, int maxLength, HorizontalHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks, Direction facing) {
        BlockPos.Mutable workingPos = new BlockPos.Mutable().set(originPos);
        BlockState stemState = config.stemBlock();
        if (stemState.contains(Properties.AXIS)) {
            stemState = stemState.with(Properties.AXIS, facing.getAxis());
        }

        // place block below stem if possible & if stem is long enough
        if (maxLength > 2) setBlockStateIfPossible(world, originPos.down(), stemState, replaceableBlocks, featureUsedBlocks);

        // place main stem segment - goes to 1 less than max length
        for (int horizDiff = 0; horizDiff < maxLength - 1; horizDiff++) {
            workingPos = originPos.offset(facing, horizDiff).mutableCopy();
            // ramps up height of generation as you go
            workingPos.setY(originPos.getY() + horizDiff / (maxLength / 2));
            setBlockStateIfPossible(world, workingPos, stemState, replaceableBlocks, featureUsedBlocks);
        }

        return workingPos.toImmutable();
    }

    // returns last block generated in center line of cap
    private BlockPos generateCap(StructureWorldAccess world, BlockPos stemFinishPos, Random random, int maxLength, HorizontalHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks, Direction facing) {
        BlockPos startingPos = stemFinishPos.offset(facing);
        BlockPos.Mutable workingPos = new BlockPos.Mutable().set(startingPos);
        BlockState wartState = config.wartBlock();

        Direction.Axis perpendicularAxis = facing.getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

        for (int horizDiff = 0; horizDiff < maxLength + 1 ; horizDiff++) {
            workingPos = startingPos.offset(facing.getOpposite(), horizDiff).mutableCopy();
            workingPos.setY(startingPos.getY() + horizDiff / (maxLength / 2));

            // place center line of growth and accent line above it - break if spine fails to place
            if (!setBlockStateIfPossible(world, workingPos, wartState, replaceableBlocks, featureUsedBlocks)) break;
            setBlockStateIfPossible(world, workingPos.up(), wartState, replaceableBlocks, featureUsedBlocks);

            // place blocks to the side of the
            for (Direction.AxisDirection direction : Direction.AxisDirection.values()) {
                Direction offsetDir = Direction.from(perpendicularAxis, direction);
                int radius = maxLength / 2;
                for (int i = 0; i < radius; i++) {
                    BlockPos targetPos = workingPos.offset(offsetDir, i + 1);
                    // wack function to make stuff deteriorate the farther it is from the origin point
                    if (i == 0 || random.nextFloat() < (0.8 - (0.05 * horizDiff) - (0.1 * i))) {
                        if (setBlockStateIfPossible(world, targetPos, wartState, replaceableBlocks, featureUsedBlocks) && i == radius - 1) {
                            generateDroop(world, targetPos.down(), random, maxLength, config, replaceableBlocks, featureUsedBlocks, facing);
                        }
                    }
                }
            }
        }

        return workingPos;
    }

    private void generateDroop(StructureWorldAccess world, BlockPos originPos, Random random, int maxLength, HorizontalHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks, Direction facing) {
        BlockState wartState = config.wartBlock();
        BlockState podState = config.podBlock();
        if (podState.contains(Properties.FACING)) {
            podState = podState.with(Properties.FACING, Direction.UP);
        }

        for (int i = 0; i < maxLength / 2; i++) {
            if (random.nextFloat() > 0.3f * i) {
                // try to place growth - if this placement rolls to succeed but can't replace block, break out of loop.
                if (random.nextFloat() < 0.8) if (!setBlockStateIfPossible(world, originPos.down(i), wartState, replaceableBlocks, featureUsedBlocks)) break;
            } else {
                // i love rng. place pod and supporting block
                if (random.nextFloat() < 0.8) {
                    setBlockStateIfPossible(world, originPos.down(i - 1), wartState, replaceableBlocks, featureUsedBlocks);
                    setBlockStateIfPossible(world, originPos.down(i), podState, replaceableBlocks, featureUsedBlocks);
                }
                break;
            }
        }
    }

    private static boolean isReplaceable(StructureWorldAccess world, BlockPos pos, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks) {
        if (world.testBlockState(pos, AbstractBlock.AbstractBlockState::isReplaceable)) {
            return true;
        } else {
            return replaceableBlocks.or(featureUsedBlocks).test(world, pos);
        }
    }

    // returns true if operation was successful - false if it wasn't
    private boolean setBlockStateIfPossible(StructureWorldAccess world, BlockPos pos, BlockState state, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks) {
        // preserve waterlogged status if possible
        if (state.contains(Properties.WATERLOGGED) && world.getFluidState(pos).isOf(Fluids.WATER)) {
            state = state.with(Properties.WATERLOGGED, true);
        }

        if (isReplaceable(world, pos, replaceableBlocks, featureUsedBlocks)) {
            setBlockState(world, pos, state);
            return true;
        } else {
            return false;
        }
    }
}
