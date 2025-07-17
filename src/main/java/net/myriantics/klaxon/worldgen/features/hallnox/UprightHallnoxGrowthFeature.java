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
import net.myriantics.klaxon.util.BlockDirectionHelper;
import org.jetbrains.annotations.Nullable;

public class UprightHallnoxGrowthFeature extends Feature<UprightHallnoxGrowthFeatureConfig> {

    public UprightHallnoxGrowthFeature(Codec<UprightHallnoxGrowthFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<UprightHallnoxGrowthFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        UprightHallnoxGrowthFeatureConfig config = context.getConfig();
        BlockState denseStemBlock = config.denseStemBlock();
        BlockState stemBlock = config.stemBlock();
        BlockState wartBlock = config.wartBlock();
        BlockState podBlock = config.podBlock();
        BlockPredicate replaceableBlocks = config.replaceableBlocks();
        BlockPredicate featureUsedBlocks = BlockPredicate.matchingBlocks(denseStemBlock.getBlock(), stemBlock.getBlock(), wartBlock.getBlock(), podBlock.getBlock());
        BlockPos originPos = context.getOrigin();
        Random random = context.getRandom();
        // throw in a bit more rng for shits and gigs
        int maxHeight = (int) (config.maxHeight() * (0.7 + (0.5 * random.nextFloat())));
        maxHeight = Math.min(maxHeight, config.maxHeight());
        int frondScale = maxHeight - 1;

        // validate that we can place growth before doing pricier calculations
        for (int yDiff = 0; yDiff < maxHeight; yDiff++) {
            if (!isReplaceable(structureWorldAccess, originPos.withY(originPos.getY() + yDiff), replaceableBlocks, featureUsedBlocks)) {
                // don't even try to generate such a small growth
                if (yDiff < 4) return false;
                maxHeight = yDiff + 1;
                break;
            }
        }

        // if max height is less than 4, don't generate base
        int baseHeight = maxHeight > 2 ? generateBase(structureWorldAccess, originPos, random, maxHeight, config, replaceableBlocks, featureUsedBlocks) : 0;
        BlockPos stemTopPos = generateStem(structureWorldAccess, originPos, random, baseHeight, maxHeight, config, replaceableBlocks, featureUsedBlocks);

        if (stemTopPos == null) return false;

        // generate fronds
        for (Direction direction : BlockDirectionHelper.HORIZONTAL) {
            BlockPos lastFrondPos = generateFrond(structureWorldAccess, stemTopPos, random, frondScale, config, replaceableBlocks, featureUsedBlocks, direction);
            generateDroop(structureWorldAccess, lastFrondPos, random, frondScale, config, replaceableBlocks, featureUsedBlocks, direction);
        }

        // prep pod state
        if (podBlock.contains(Properties.FACING)) {
            podBlock = podBlock.with(Properties.FACING, Direction.DOWN);
        }

        // place pod on top
        setBlockStateIfPossible(structureWorldAccess, stemTopPos.up(), podBlock, replaceableBlocks, featureUsedBlocks);

        return true;
    }

    private int generateBase(StructureWorldAccess world, BlockPos originPos, Random random, int maxHeight, UprightHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks) {
        BlockPos.Mutable workingPos = new BlockPos.Mutable().set(originPos);

        // prep the dense stem state
        BlockState denseStemState = config.denseStemBlock();
        if (denseStemState.contains(Properties.AXIS)) {
            denseStemState = denseStemState.with(Properties.AXIS, Direction.Axis.Y);
        }

        int baseHeight = (int) (maxHeight * 0.45);

        for (int yDiff = 0; yDiff < baseHeight; yDiff++) {
            workingPos.setY(originPos.getY() + yDiff);

            for (BlockPos selected : BlockPos.iterateInSquare(workingPos, 1, Direction.NORTH, Direction.EAST)) {
                // blocks aligned with origin pos are guaranteed to place
                // blocks that share an axis have an 85% chance to place
                // blocks on corners have a 70& chance to place
                double placementChance = 0.7;
                placementChance += selected.getX() == workingPos.getX() ? 0.15 : 0;
                placementChance += selected.getZ() == workingPos.getZ() ? 0.15 : 0;
                // make sure block can be replaced and roll for block placement
                if (random.nextFloat() < placementChance) setBlockStateIfPossible(world, selected, denseStemState, replaceableBlocks, featureUsedBlocks);
            }
        }

        return baseHeight;
    }

    // returns top middle block of stem
    private @Nullable BlockPos generateStem(StructureWorldAccess world, BlockPos originPos, Random random, int baseHeight, int maxHeight, UprightHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks) {
        BlockPos.Mutable workingPos = new BlockPos.Mutable().set(originPos);

        // prep stem state
        BlockState stemState = config.stemBlock();
        if (stemState.contains(Properties.AXIS)) {
            stemState = stemState.with(Properties.AXIS, Direction.Axis.Y);
        }

        // only start placing stem from where the base placer left off
        for (int yDiff = baseHeight; yDiff < maxHeight; yDiff++) {
            workingPos.setY(originPos.getY() + yDiff);

            if (!setBlockStateIfPossible(world, workingPos, stemState, replaceableBlocks, featureUsedBlocks)) return null;
        }

        for (Direction direction : BlockDirectionHelper.HORIZONTAL) {
            if (random.nextFloat() > 0.7) setBlockStateIfPossible(world, workingPos.offset(direction).down(), stemState, replaceableBlocks, featureUsedBlocks);
        }

        return workingPos.toImmutable();
    }

    // returns middle block of generated frond
    private BlockPos generateFrond(StructureWorldAccess world, BlockPos stemTopPos, Random random, int frondScale, UprightHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks, Direction direction) {
        BlockState wartState = config.wartBlock();
        Direction.Axis perpendicularAxis = direction.getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

        BlockPos.Mutable workingPos = stemTopPos.mutableCopy();

        for (int horizDiff = 0; horizDiff < frondScale; horizDiff++) {
            // hacky and awkward but it works
            switch (direction.getAxis()) {
                case X -> workingPos.setX(stemTopPos.getX() + (direction.equals(Direction.EAST) ? horizDiff : -horizDiff));
                case Z -> workingPos.setZ(stemTopPos.getZ() + (direction.equals(Direction.NORTH) ? horizDiff : -horizDiff));
            }

            // place main row as well as line of blocks above it - if main spine is interrupted, break.
            if (!setBlockStateIfPossible(world, workingPos, wartState, replaceableBlocks, featureUsedBlocks)) break;
            setBlockStateIfPossible(world, workingPos.up(), wartState, replaceableBlocks, featureUsedBlocks);

            // place blocks to the sides of main frond
            for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
                BlockPos offsetPos = workingPos.offset(Direction.from(perpendicularAxis, axisDirection), 1);
                if (random.nextFloat() < 0.9) setBlockStateIfPossible(world, offsetPos, wartState, replaceableBlocks, featureUsedBlocks);

                // place middle ring
                if (horizDiff == 1) setBlockStateIfPossible(world, offsetPos.up(), wartState, replaceableBlocks, featureUsedBlocks);
            }
        }

        // return last mainline block placed in frond
        return workingPos.toImmutable();
    }

    private void generateDroop(StructureWorldAccess world, BlockPos frondFinalPos, Random random, int frondScale, UprightHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, BlockPredicate featureUsedBlocks, Direction facing) {
        // prep states for placement
        BlockState wartState = config.wartBlock();
        BlockState podState = config.podBlock();
        if (podState.contains(Properties.FACING)) {
            podState = podState.with(Properties.FACING, Direction.UP);
        }

        BlockPos.Mutable workingPos = frondFinalPos.mutableCopy();

        for (int yDiff = 0; yDiff < frondScale - 1; yDiff++) {
            workingPos.setY(frondFinalPos.getY() - yDiff);

            // place droop center if possible - stop trying to place droop if failed
            if (!setBlockStateIfPossible(world, workingPos, wartState, replaceableBlocks, featureUsedBlocks)) break;
            if (yDiff == 1 && random.nextFloat() < 0.8) setBlockStateIfPossible(world, workingPos.offset(facing.getAxis().equals(Direction.Axis.Z) ? facing : facing.getOpposite()), wartState, replaceableBlocks, featureUsedBlocks);

            // used to make fronds wider
            Direction.Axis perpendicularAxis = facing.getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

            // don't generate droop sides past a certain point
            if (yDiff == frondScale - 2) continue;

            // place blocks on side of droops
            for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
                Direction placingDirection = Direction.from(perpendicularAxis, axisDirection);
                BlockPos offsetPos = workingPos.offset(placingDirection, 1);

                if (random.nextFloat() < 0.9) {
                    boolean success = setBlockStateIfPossible(world, offsetPos, wartState, replaceableBlocks, featureUsedBlocks);

                    // if we successfully placed one of the ending droop parts, roll for a hallnox pod to spawn
                    if (success && random.nextFloat() < 0.5) {
                        setBlockStateIfPossible(world, offsetPos.down(), podState, replaceableBlocks, featureUsedBlocks);
                    }
                }
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
