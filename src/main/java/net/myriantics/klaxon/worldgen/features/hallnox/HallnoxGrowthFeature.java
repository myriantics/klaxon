package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.block.NeighborUpdater;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;

import java.util.Iterator;
import java.util.List;

public class HallnoxGrowthFeature extends Feature<HallnoxGrowthFeatureConfig> {

    private final int MAX_HEIGHT = 10;
    private final int STEM_MAX_HEIGHT = 6;

    public HallnoxGrowthFeature(Codec<HallnoxGrowthFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<HallnoxGrowthFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        HallnoxGrowthFeatureConfig config = context.getConfig();
        BlockPredicate replaceableBlocks = config.replaceableBlocks();
        BlockPos originPos = context.getOrigin();
        Random random = context.getRandom();

        generateStem(structureWorldAccess, originPos, random, config);
        generateTop(structureWorldAccess, originPos, random, config);

        return true;
    }

    private void generateStem(StructureWorldAccess world, BlockPos originPos, Random random, HallnoxGrowthFeatureConfig config) {
        BlockPos.Mutable workingPos = new BlockPos.Mutable().set(originPos);

        for (int i = 0; i < STEM_MAX_HEIGHT; i++) {
            // whack ass function i cooked in desmos that defines tree shape
            int radius = (int) (6 * (((i - 5)^2 )* 0.03 + 0.25));

            workingPos.setY(originPos.getY() - 1 + i);

            for (BlockPos selected : BlockPos.iterateInSquare(workingPos, radius, Direction.NORTH, Direction.EAST)) {

                float f = (float) (Math.sqrt(selected.getSquaredDistance(workingPos)) - radius);
                if (random.nextFloat() > f) this.setBlockState(world, selected, KlaxonBlocks.STEEL_PLATING_BLOCK.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y));

            }
        }
    }

    private void generateTop(StructureWorldAccess world, BlockPos originPos, Random random, HallnoxGrowthFeatureConfig config) {
        BlockPos.Mutable workingPos = new BlockPos.Mutable().set(originPos);

        for (int i = STEM_MAX_HEIGHT; i < MAX_HEIGHT; i++) {
            // whack ass function i cooked in desmos that defines tree shape
            int radius = (int) (6 * (((i - 5)^2 )* 0.03 + 0.25));

            workingPos.setY(originPos.getY() - 1 + i);

            for (BlockPos selected : BlockPos.iterateInSquare(workingPos, radius, Direction.NORTH, Direction.EAST)) {

                float f = (float) (Math.sqrt(selected.getSquaredDistance(workingPos)) - radius);
                if (random.nextFloat() > f) this.setBlockState(world, selected, KlaxonBlocks.STEEL_CASING.getDefaultState());

            }
        }
    }
}
