package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.myriantics.klaxon.block.customblocks.decor.HallnoxPodBlock;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.util.BlockDirectionHelper;

public class UprightDryHallnoxGrowthFeature extends Feature<UprightDryHallnoxGrowthFeatureConfig> {

    public UprightDryHallnoxGrowthFeature(Codec<UprightDryHallnoxGrowthFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<UprightDryHallnoxGrowthFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        UprightDryHallnoxGrowthFeatureConfig config = context.getConfig();
        BlockPredicate replaceableBlocks = config.replaceableBlocks();
        BlockPos originPos = context.getOrigin();
        Random random = context.getRandom();
        // throw in a bit more rng for shits and gigs
        int maxHeight = (int) (config.maxHeight() * (0.7 + (0.5 * random.nextFloat())));
        maxHeight = Math.min(maxHeight, config.maxHeight());
        int frondScale = maxHeight - 1;

        // validate that we can place growth before doing pricier calculations
        for (int yDiff = 0; yDiff < maxHeight; yDiff++) {
            if (!replaceableBlocks.test(structureWorldAccess, originPos.withY(originPos.getY() - yDiff))) {
                maxHeight -= yDiff;
                break;
            }
        }

        // if max height is less than 4, don't generate base
        int baseHeight = maxHeight > 2 ? generateBase(structureWorldAccess, originPos, random, maxHeight, config, replaceableBlocks) : 0;
        BlockPos stemTopPos = generateStem(structureWorldAccess, originPos, random, baseHeight, maxHeight, config, replaceableBlocks);

        // generate fronds
        for (Direction direction : BlockDirectionHelper.HORIZONTAL) {
            BlockPos lastFrondPos = generateFrond(structureWorldAccess, stemTopPos, random, frondScale, config, replaceableBlocks, direction);
            generateDroop(structureWorldAccess, lastFrondPos, random, frondScale, config, replaceableBlocks, direction);
        }

        // place pod on top
        setBlockStateIfPossible(structureWorldAccess, stemTopPos.up(), KlaxonBlocks.HALLNOX_POD.getDefaultState().with(HallnoxPodBlock.FACING, Direction.DOWN), replaceableBlocks);

        return true;
    }

    private int generateBase(StructureWorldAccess world, BlockPos originPos, Random random, int maxHeight, UprightDryHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks) {
        BlockPos.Mutable workingPos = new BlockPos.Mutable().set(originPos);

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
                if (random.nextFloat() < placementChance) setBlockState(world, selected, KlaxonBlocks.STEEL_PLATING_BLOCK.getDefaultState());
            }
        }

        return baseHeight;
    }

    // returns top middle block of stem
    private BlockPos generateStem(StructureWorldAccess world, BlockPos originPos, Random random, int baseHeight, int maxHeight, UprightDryHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks) {
        BlockPos.Mutable workingPos = new BlockPos.Mutable().set(originPos);

        // only start placing stem from where the base placer left off
        for (int yDiff = baseHeight; yDiff < maxHeight; yDiff++) {
            workingPos.setY(originPos.getY() + yDiff);

            setBlockStateIfPossible(world, workingPos, KlaxonBlocks.STEEL_PLATING_BLOCK.getDefaultState(), replaceableBlocks);
        }

        for (Direction direction : BlockDirectionHelper.HORIZONTAL) {
            if (random.nextFloat() > 0.7) setBlockStateIfPossible(world, workingPos.offset(direction).down(), KlaxonBlocks.STEEL_PLATING_BLOCK.getDefaultState(), replaceableBlocks);
        }

        return workingPos.toImmutable();
    }

    // returns middle block of generated frond
    private BlockPos generateFrond(StructureWorldAccess world, BlockPos stemTopPos, Random random, int frondScale, UprightDryHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, Direction direction) {
        BlockState wartState = KlaxonBlocks.STEEL_CASING.getDefaultState();
        Direction.Axis perpendicularAxis = direction.getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

        BlockPos.Mutable workingPos = stemTopPos.mutableCopy();

        for (int horizDiff = 0; horizDiff < frondScale; horizDiff++) {
            // hacky and awkward but it works
            switch (direction.getAxis()) {
                case X -> workingPos.setX(stemTopPos.getX() + (direction.equals(Direction.EAST) ? horizDiff : -horizDiff));
                case Z -> workingPos.setZ(stemTopPos.getZ() + (direction.equals(Direction.NORTH) ? horizDiff : -horizDiff));
            }

            // place main row as well as line of blocks above it
            setBlockStateIfPossible(world, workingPos, wartState, replaceableBlocks);
            setBlockStateIfPossible(world, workingPos.up(), wartState, replaceableBlocks);

            // place blocks to the sides of main frond
            for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
                BlockPos offsetPos = workingPos.offset(Direction.from(perpendicularAxis, axisDirection), 1);
                if (random.nextFloat() < 0.9) setBlockStateIfPossible(world, offsetPos, wartState, replaceableBlocks);

                // place middle ring
                if (horizDiff == 1) setBlockStateIfPossible(world, offsetPos.up(), wartState, replaceableBlocks);
            }
        }

        // return last mainline block placed in frond
        return workingPos.toImmutable();
    }

    private void generateDroop(StructureWorldAccess world, BlockPos frondFinalPos, Random random, int frondScale, UprightDryHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, Direction facing) {
        BlockState placedState = KlaxonBlocks.STEEL_CASING.getDefaultState();

        BlockPos.Mutable workingPos = frondFinalPos.mutableCopy();

        for (int yDiff = 0; yDiff < frondScale - 1; yDiff++) {
            workingPos.setY(frondFinalPos.getY() - yDiff);

            // place droop center
            setBlockStateIfPossible(world, workingPos, placedState, replaceableBlocks);
            if (yDiff == 1 && random.nextFloat() < 0.8) setBlockStateIfPossible(world, workingPos.offset(facing.getAxis().equals(Direction.Axis.Z) ? facing : facing.getOpposite()), placedState, replaceableBlocks);

            // used to make fronds wider
            Direction.Axis perpendicularAxis = facing.getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

            // don't generate droop sides past a certain point
            if (yDiff == frondScale - 2) continue;

            // place blocks on side of droops
            for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
                Direction placingDirection = Direction.from(perpendicularAxis, axisDirection);
                BlockPos offsetPos = workingPos.offset(placingDirection, 1);

                if (random.nextFloat() < 0.9) {
                    boolean success = setBlockStateIfPossible(world, offsetPos, placedState, replaceableBlocks);

                    // if we successfully placed one of the ending droop parts, roll for a hallnox pod to spawn
                    if (success && random.nextFloat() < 0.5) {
                        setBlockStateIfPossible(world, offsetPos.down(), KlaxonBlocks.HALLNOX_POD.getDefaultState().with(HallnoxPodBlock.FACING, Direction.UP), replaceableBlocks);
                    }
                }
            }
        }
    }

    // returns true if operation was successful - false if it wasn't
    private boolean setBlockStateIfPossible(StructureWorldAccess world, BlockPos pos, BlockState state, BlockPredicate replaceableBlocks) {
        if (replaceableBlocks.test(world, pos)) {
            setBlockState(world, pos, state);
            return true;
        } else {
            return false;
        }
    }
}
