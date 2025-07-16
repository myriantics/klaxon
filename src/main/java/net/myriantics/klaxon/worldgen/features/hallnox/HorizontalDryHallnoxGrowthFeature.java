package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.decor.HallnoxPodBlock;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.util.BlockDirectionHelper;

public class HorizontalDryHallnoxGrowthFeature extends Feature<HorizontalDryHallnoxGrowthFeatureConfig> {
    public HorizontalDryHallnoxGrowthFeature(Codec<HorizontalDryHallnoxGrowthFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<HorizontalDryHallnoxGrowthFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        HorizontalDryHallnoxGrowthFeatureConfig config = context.getConfig();
        BlockPredicate replaceableBlocks = config.replaceableBlocks();
        BlockPos originPos = context.getOrigin();
        Random random = context.getRandom();
        Direction facing = config.horizontalFacing();
        int maxLength = 5;

        // make sure we can actually spawn stuff - cap height if not
        for (int horizDiff = 0; horizDiff < maxLength - 1; horizDiff++) {
            if (!replaceableBlocks.test(structureWorldAccess, originPos.offset(facing, horizDiff))) {
                maxLength -= horizDiff;
                break;
            }
        }

        BlockPos stemEndPos = generateStem(structureWorldAccess, originPos, random, maxLength, config, replaceableBlocks, facing);
        generateCap(structureWorldAccess, stemEndPos, random, maxLength, config, replaceableBlocks, facing);

        return true;
    }

    // returns ending block of stem
    private BlockPos generateStem(StructureWorldAccess world, BlockPos originPos, Random random, int maxLength, HorizontalDryHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, Direction facing) {
        BlockPos.Mutable workingPos = new BlockPos.Mutable().set(originPos);
        BlockState placedState = KlaxonBlocks.STEEL_PLATING_BLOCK.getDefaultState().with(PillarBlock.AXIS, facing.getAxis());

        // place block below stem if possible & if stem is long enough
        if (maxLength > 2) setBlockStateIfPossible(world, originPos.down(), placedState, replaceableBlocks);

        // place main stem segment - goes to 1 less than max length
        for (int horizDiff = 0; horizDiff < maxLength - 1; horizDiff++) {
            workingPos = originPos.offset(facing, horizDiff).mutableCopy();
            // ramps up height of generation as you go
            workingPos.setY(originPos.getY() + horizDiff / (maxLength / 2));
            setBlockStateIfPossible(world, workingPos, placedState, replaceableBlocks);
        }

        return workingPos.toImmutable();
    }

    // returns last block generated in center line of cap
    private BlockPos generateCap(StructureWorldAccess world, BlockPos stemFinishPos, Random random, int maxLength, HorizontalDryHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, Direction facing) {
        BlockPos startingPos = stemFinishPos.offset(facing);
        BlockPos.Mutable workingPos = new BlockPos.Mutable().set(startingPos);
        BlockState placedState = KlaxonBlocks.STEEL_CASING.getDefaultState();

        Direction.Axis perpendicularAxis = facing.getAxis().equals(Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;

        for (int horizDiff = 0; horizDiff < maxLength + 1 ; horizDiff++) {
            workingPos = startingPos.offset(facing.getOpposite(), horizDiff).mutableCopy();
            workingPos.setY(startingPos.getY() + horizDiff / (maxLength / 2));

            // place center line of growth and accent line above it
            setBlockStateIfPossible(world, workingPos, placedState, replaceableBlocks);
            setBlockStateIfPossible(world, workingPos.up(), placedState, replaceableBlocks);

            // place blocks to the side of the
            for (Direction.AxisDirection direction : Direction.AxisDirection.values()) {
                Direction offsetDir = Direction.from(perpendicularAxis, direction);
                int radius = maxLength / 2;
                for (int i = 0; i < radius; i++) {
                    BlockPos targetPos = workingPos.offset(offsetDir, i + 1);
                    // wack function to make stuff deteriorate the farther it is from the origin point
                    if (i == 0 || random.nextFloat() < (0.8 - (0.05 * horizDiff) - (0.1 * i))) {
                        if (setBlockStateIfPossible(world, targetPos, placedState, replaceableBlocks) && i == radius - 1) {
                            generateDroop(world, targetPos.down(), random, maxLength, config, replaceableBlocks, facing);
                        }
                    }
                }
            }
        }

        return workingPos;
    }

    private void generateDroop(StructureWorldAccess world, BlockPos originPos, Random random, int maxLength, HorizontalDryHallnoxGrowthFeatureConfig config, BlockPredicate replaceableBlocks, Direction facing) {
        for (int i = 0; i < maxLength / 2; i++) {
            if (random.nextFloat() > 0.3f * i) {
                if (random.nextFloat() < 0.8) setBlockStateIfPossible(world, originPos.down(i), KlaxonBlocks.STEEL_CASING.getDefaultState(), replaceableBlocks);
            } else {
                if (random.nextFloat() < 0.6) {
                    setBlockStateIfPossible(world, originPos.down(i - 1), KlaxonBlocks.STEEL_CASING.getDefaultState(), replaceableBlocks);
                    setBlockStateIfPossible(world, originPos.down(i), KlaxonBlocks.HALLNOX_POD.getDefaultState().with(HallnoxPodBlock.FACING, Direction.UP), replaceableBlocks);
                }
                break;
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
