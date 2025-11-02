package net.myriantics.klaxon_gametest.test.block.machines.blast_processor.deepslate;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

public class DeepslateBlastProcessorBlockTester {
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void test(TestContext context) {
        context.setBlockState(BlockPos.ORIGIN, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
        context.checkBlock(
                BlockPos.ORIGIN,
                (block) -> block.equals(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR),
                "Expected Deepslate Blast Processor at: " + BlockPos.ORIGIN
        );
        context.complete();
    }
}
