package net.myriantics.klaxon_gametest.test.block.machines.nether_reactor_core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.level.block.Blocks;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestHelper;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestTemplates;

public class NetherReactorCoreBlockTester {

    private static final BlockPos CORE_POS = BlockPos.ZERO.above();
    private static final BlockPos LEVER_POS = CORE_POS.above();
    private static final BlockPos BLACKSTONE_EXPECT_POS = LEVER_POS.relative(Direction.EAST);

    @GameTest(template = KlaxonGameTestTemplates.NETHER_REACTION)
    public static void testStandardNetherReactorCore(KlaxonGameTestHelper helper) {
        helper.setBlock(CORE_POS, KlaxonBlocks.NETHER_REACTOR_CORE);
        helper.pullLever(LEVER_POS);

        helper.runAtTickTime(helper.getTick() + 12, () -> {
            helper.assertBlockPresent(KlaxonBlocks.STEEL_CASING, CORE_POS);
            helper.assertBlock(BLACKSTONE_EXPECT_POS, block -> block == Blocks.BLACKSTONE, "Nether Reactor Core conversion failed!");
            helper.succeed();
        });
    }

    @GameTest(template = KlaxonGameTestTemplates.NETHER_REACTION)
    public static void testCrudeNetherReactorCore(KlaxonGameTestHelper helper) {
        helper.setBlock(CORE_POS, KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE);
        helper.pullLever(LEVER_POS);

        helper.runAtTickTime(helper.getTick() + 12, () -> {
            helper.assertBlockNotPresent(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE, CORE_POS);
            helper.assertBlock(BLACKSTONE_EXPECT_POS, block -> block == Blocks.BLACKSTONE, "Nether Reactor Core conversion failed!");
            helper.succeed();
        });
    }
}
