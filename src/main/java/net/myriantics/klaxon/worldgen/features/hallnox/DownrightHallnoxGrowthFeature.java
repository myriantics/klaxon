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
import net.myriantics.klaxon.util.BlockDirectionHelper;

public class DownrightHallnoxGrowthFeature extends Feature<DownrightHallnoxGrowthFeatureConfig> {
    public DownrightHallnoxGrowthFeature(Codec<DownrightHallnoxGrowthFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<DownrightHallnoxGrowthFeatureConfig> context) {
        WorldGenLevel structureWorldAccess = context.level();
        DownrightHallnoxGrowthFeatureConfig config = context.config();
        BlockState denseStemBlock = config.denseStemBlock();
        BlockState stemBlock = config.stemBlock();
        BlockState wartBlock = config.wartBlock();
        BlockState podBlock = config.podBlock();
        BlockPredicate replaceableBlocks = config.replaceableBlocks();
        BlockPredicate featureUsedBlocks = BlockPredicate.matchesBlocks(denseStemBlock.getBlock(), stemBlock.getBlock(), wartBlock.getBlock(), podBlock.getBlock());
        BlockPos originPos = context.origin();
        RandomSource random = context.random();
        // throw in a bit more rng for shits and gigs
        int maxDepth = (int) (config.maxDepth() * (0.7 + (0.5 * random.nextFloat())));
        maxDepth = Math.min(maxDepth, config.maxDepth());
        int frondScale = (int) (maxDepth * (0.4 + (0.7 * random.nextFloat())));
        frondScale = Math.min(frondScale, maxDepth);

        // validate that we can place growth before doing pricier calculations
        for (int yDiff = 0; yDiff < maxDepth; yDiff++) {
            if (!isReplaceable(structureWorldAccess, originPos.atY(originPos.getY() - yDiff), replaceableBlocks, featureUsedBlocks)) {
                // don't even try to generate such a small growth
                if (yDiff < 4) return false;
                maxDepth = yDiff + 1;
                break;
            }
        }

        // if max depth is less than 4, don't generate base
        int baseHeight = maxDepth > 2 ? generateBase(structureWorldAccess, originPos, random, maxDepth, config, replaceableBlocks, featureUsedBlocks) : 0;
        BlockPos stemEndPos = generateStem(structureWorldAccess, originPos, random, baseHeight, maxDepth, config, replaceableBlocks, featureUsedBlocks);

        // generate fronds
        for (Direction direction : BlockDirectionHelper.HORIZONTAL) {
            BlockPos lastFrondPos = generateFrond(structureWorldAccess, stemEndPos, random, frondScale, config, replaceableBlocks, featureUsedBlocks, direction);
            generateDroop(structureWorldAccess, lastFrondPos, random, frondScale, config, replaceableBlocks, featureUsedBlocks, direction);
        }

        // this one is more reinforced because it's hanging from the ceiling
        generateReinforcements(structureWorldAccess, stemEndPos, random, maxDepth, config, replaceableBlocks, featureUsedBlocks);

        // prep pod state
        if (podBlock.hasProperty(BlockStateProperties.FACING)) {
            podBlock = podBlock.setValue(BlockStateProperties.FACING, Direction.UP);
        }

        // place pod on bottom - if that fails, keep trying until it places.
        BlockPos.MutableBlockPos podPlacementPos = stemEndPos.below().mutable();
        while (!setBlockStateIfPossible(structureWorldAccess, podPlacementPos, podBlock, replaceableBlocks, featureUsedBlocks)) {
            if (podPlacementPos.getY() >= originPos.getY()) {
                return false;
            } else {
                podPlacementPos.setY(podPlacementPos.getY() + 1);
            }
        }

        return true;
    }

    private int generateBase(WorldGenLevel world, BlockPos originPos, RandomSource random, int maxHeight, DownrightHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks) {
        BlockPos.MutableBlockPos workingPos = new BlockPos.MutableBlockPos().set(originPos);

        // prep the dense stem state
        BlockState denseStemState = config.denseStemBlock();
        if (denseStemState.hasProperty(BlockStateProperties.AXIS)) {
            denseStemState = denseStemState.setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
        }

        int baseHeight = (int) (maxHeight * 0.45);

        for (int yDiff = 0; yDiff < baseHeight; yDiff++) {
            workingPos.setY(originPos.getY() - yDiff);

            for (BlockPos selected : BlockPos.spiralAround(workingPos, 1, Direction.NORTH, Direction.EAST)) {
                // blocks aligned with origin pos are guaranteed to place
                // blocks that share an axis have an 92.5% chance to place
                // blocks on corners have a 85% chance to place
                // chance is greater for placement than upright version because this has to hold on to the cieling
                double placementChance = 0.85;
                placementChance += selected.getX() == workingPos.getX() ? 0.075 : 0;
                placementChance += selected.getZ() == workingPos.getZ() ? 0.075 : 0;
                // make sure block can be replaced and roll for block placement
                if (random.nextFloat() < placementChance) setBlockStateIfPossible(world, selected, denseStemState, replaceableBlocks, featureUsedBlocks);
            }
        }

        return baseHeight;
    }

    // returns top middle block of stem
    private BlockPos generateStem(WorldGenLevel world, BlockPos originPos, RandomSource random, int baseDepth, int maxDepth, DownrightHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks) {
        BlockPos.MutableBlockPos workingPos = new BlockPos.MutableBlockPos().set(originPos);

        // prep stem state
        BlockState stemState = config.stemBlock();
        if (stemState.hasProperty(BlockStateProperties.AXIS)) {
            stemState = stemState.setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
        }

        // only start placing stem from where the base placer left off
        for (int yDiff = baseDepth; yDiff < maxDepth; yDiff++) {
            workingPos.setY(originPos.getY() - yDiff);

            if (!setBlockStateIfPossible(world, workingPos, stemState, replaceableBlocks, featureUsedBlocks)) break;
        }

        return workingPos.immutable();
    }

    private void generateReinforcements(WorldGenLevel world, BlockPos stemEndPos, RandomSource random, int maxDepth, DownrightHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks) {
        BlockPos.MutableBlockPos workingPos = stemEndPos.below(2).mutable();

        BlockState wartState = config.wartBlock();

        // place reinforcements
        for (int yDiff = 0; yDiff < maxDepth + 1; yDiff++) {
            if (yDiff < (maxDepth * 0.6)) {
                for (Direction direction : BlockDirectionHelper.HORIZONTAL) {
                    if (random.nextFloat() < 0.7) setBlockStateIfPossible(world, workingPos.relative(direction).above(yDiff), wartState, replaceableBlocks, featureUsedBlocks);
                }
            }
        }
    }

    // returns middle block of generated frond
    private BlockPos generateFrond(WorldGenLevel world, BlockPos stemEndPos, RandomSource random, int frondScale, DownrightHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks, Direction direction) {
        BlockState wartState = config.wartBlock();
        Direction.Axis perpendicularAxis = direction.getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

        BlockPos.MutableBlockPos workingPos = stemEndPos.mutable();

        for (int horizDiff = 0; horizDiff < frondScale; horizDiff++) {
            // hacky and awkward but it works
            switch (direction.getAxis()) {
                case X -> workingPos.setX(stemEndPos.getX() + (direction.equals(Direction.EAST) ? horizDiff : -horizDiff));
                case Z -> workingPos.setZ(stemEndPos.getZ() + (direction.equals(Direction.NORTH) ? horizDiff : -horizDiff));
            }

            // place line of blocks above main row
            setBlockStateIfPossible(world, workingPos.above(), wartState, replaceableBlocks, featureUsedBlocks);
            // if placement of main row failed, offset working Y pos by 1 for better visuals
            if (!setBlockStateIfPossible(world, workingPos, wartState, replaceableBlocks, featureUsedBlocks)) {
                // no second chances - only try to bump the working pos up by 1 before breaking out of loop
                if (workingPos.getY() != stemEndPos.getY()) break;
                workingPos.setY(stemEndPos.getY() + 1);
            }

            // place blocks to the sides of main frond
            for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
                // these are wider because why not
                for (int i = 0; i < 2; i++) {
                    BlockPos offsetPos = workingPos.relative(Direction.fromAxisAndDirection(perpendicularAxis, axisDirection), i + 1);
                    if (random.nextFloat() < 0.8) setBlockStateIfPossible(world, offsetPos, wartState, replaceableBlocks, featureUsedBlocks);

                }
                // place middle ring
                if (horizDiff == 1) setBlockStateIfPossible(world, workingPos.relative(Direction.fromAxisAndDirection(perpendicularAxis, axisDirection), 1), wartState, replaceableBlocks, featureUsedBlocks);
            }
        }

        // return last mainline block placed in frond
        return workingPos.immutable();
    }

    private void generateDroop(WorldGenLevel world, BlockPos frondFinalPos, RandomSource random, int frondScale, DownrightHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks, Direction facing) {
        // prep states for placement
        BlockState wartState = config.wartBlock();
        BlockState podState = config.podBlock();
        if (podState.hasProperty(BlockStateProperties.FACING)) {
            podState = podState.setValue(BlockStateProperties.FACING, Direction.UP);
        }

        BlockPos.MutableBlockPos workingPos = frondFinalPos.mutable();

        for (int yDiff = 0; yDiff < frondScale - 1; yDiff++) {
            workingPos.setY(frondFinalPos.getY() - yDiff);

            // place droop center if possible - stop trying to place droop if failed
            if (!setBlockStateIfPossible(world, workingPos, wartState, replaceableBlocks, featureUsedBlocks)) {
                // try to place a pod as a fallback
                if (random.nextFloat() < 0.4) setBlockStateIfPossible(world, frondFinalPos, podState, replaceableBlocks, featureUsedBlocks);
                break;
            }
            if (yDiff == 1 && random.nextFloat() < 0.8) setBlockStateIfPossible(world, workingPos.relative(facing.getAxis().equals(Direction.Axis.Z) ? facing : facing.getOpposite()), wartState, replaceableBlocks, featureUsedBlocks);

            // used to make fronds wider
            Direction.Axis perpendicularAxis = facing.getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

            // don't generate droop sides past a certain point
            if (yDiff == frondScale - 2) continue;

            // place blocks on side of droops
            for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
                Direction placingDirection = Direction.fromAxisAndDirection(perpendicularAxis, axisDirection);
                BlockPos offsetPos = workingPos.relative(placingDirection, 1);

                if (random.nextFloat() < 0.9) {
                    boolean success = setBlockStateIfPossible(world, offsetPos, wartState, replaceableBlocks, featureUsedBlocks);

                    // if we successfully placed one of the ending droop parts, roll for a hallnox pod to spawn
                    if (success && random.nextFloat() < 0.5) {
                        setBlockStateIfPossible(world, offsetPos.below(), podState, replaceableBlocks, featureUsedBlocks);
                    }
                }
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
