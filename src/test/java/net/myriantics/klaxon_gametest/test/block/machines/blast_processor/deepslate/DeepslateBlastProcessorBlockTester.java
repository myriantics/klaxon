package net.myriantics.klaxon_gametest.test.block.machines.blast_processor.deepslate;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon_gametest.util.KlaxonTestContext;

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

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void testFastInput(KlaxonTestContext context) {
        BlockPos pos = BlockPos.ORIGIN;
        context.setBlockState(pos, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.getDefaultState());
        FakePlayer player = context.createFakePlayer(GameMode.SURVIVAL);
        ItemStack gunpowderStack = new ItemStack(Items.GUNPOWDER, 64);
        ItemStack coalStack = new ItemStack(Items.COAL, 64);

        // test top fastinput with survival mode
        player.setStackInHand(Hand.MAIN_HAND, coalStack);
        context.useBlock(pos, player, context.hitResult(pos, Direction.UP));
        context.expectContainerWith(pos, Items.COAL);
        context.expectBlockProperty(pos, DeepslateBlastProcessorBlock.HATCH_OPEN, false);
        context.expectInt(63, coalStack.getCount(), "Coal Stack");

        player.changeGameMode(GameMode.CREATIVE);

        // test side fastinput with creative mode
        player.setStackInHand(Hand.MAIN_HAND, gunpowderStack);
        context.useBlock(pos, player, context.hitResult(pos, Direction.EAST));
        context.expectContainerWith(pos, Items.GUNPOWDER);
        context.expectBlockProperty(pos, DeepslateBlastProcessorBlock.FUELED, true);
        context.expectInt(64, gunpowderStack.getCount(), "Gunpowder Stack");

        context.complete();
    }
}
