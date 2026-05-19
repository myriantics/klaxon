package net.myriantics.klaxon_gametest.test.block.machines.blast_processor;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorLootState;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestHelper;

public class DeepslateBlastProcessorBlockTester {
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public static void test(KlaxonGameTestHelper context) {
        context.setBlock(BlockPos.ZERO, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.value());
        context.assertBlock(
                BlockPos.ZERO,
                (block) -> block.equals(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.value()),
                "Expected Deepslate Blast Processor at: " + BlockPos.ZERO
        );
        context.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public static void testFastInput(KlaxonGameTestHelper context) {
        BlockPos pos = BlockPos.ZERO;
        context.setBlock(pos, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.value().defaultBlockState());
        FakePlayer player = context.createFakePlayer(GameType.SURVIVAL);
        ItemStack gunpowderStack = new ItemStack(Items.GUNPOWDER, 64);
        ItemStack coalStack = new ItemStack(Items.COAL, 64);

        // test top fastinput with survival mode
        player.setItemInHand(InteractionHand.MAIN_HAND, coalStack);
        context.useBlock(pos, player, context.hitResult(pos, Direction.UP));
        context.assertContainerContains(pos, Items.COAL);
        context.assertBlockProperty(pos, DeepslateBlastProcessorBlock.LOOT_STATE, DeepslateBlastProcessorLootState.INGREDIENT_ONLY);
        context.expectInt(63, coalStack.getCount(), "Coal Stack");

        player.setGameMode(GameType.CREATIVE);

        // test side fastinput with creative mode
        player.setItemInHand(InteractionHand.MAIN_HAND, gunpowderStack);
        context.useBlock(pos, player, context.hitResult(pos, Direction.EAST));
        context.assertContainerContains(pos, Items.GUNPOWDER);
        context.assertBlockProperty(pos, DeepslateBlastProcessorBlock.LOOT_STATE, DeepslateBlastProcessorLootState.FULL);
        context.expectInt(64, gunpowderStack.getCount(), "Gunpowder Stack");

        context.succeed();
    }
}
