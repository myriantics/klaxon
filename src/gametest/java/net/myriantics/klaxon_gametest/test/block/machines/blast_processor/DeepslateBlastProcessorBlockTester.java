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
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorLootState;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.dynamic.KlaxonLootTables;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestHelper;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestTemplates;

public class DeepslateBlastProcessorBlockTester implements FabricGameTest {

    private static final BlockPos ORIGIN_POS = BlockPos.ZERO.above();

    private static final BlockPos DEEPSLATE_BLAST_PROCESSOR_POS = ORIGIN_POS.above().east().south();
    private static final BlockPos BARREL_POS = DEEPSLATE_BLAST_PROCESSOR_POS.below();
    private static final BlockPos DEEPSLATE_BLAST_PROCESSOR_TRIGGER_BUTTON_POS = DEEPSLATE_BLAST_PROCESSOR_POS.above(2).west();
    private static final BlockPos INGREDIENT_INPUT_DROPPER_POS = DEEPSLATE_BLAST_PROCESSOR_POS.south();
    private static final BlockPos INGREDIENT_INPUT_BUTTON_POS = INGREDIENT_INPUT_DROPPER_POS.below().west();
    private static final BlockPos CATALYST_INPUT_DROPPER_POS = DEEPSLATE_BLAST_PROCESSOR_POS.west();
    private static final BlockPos CATALYST_INPUT_BUTTON_POS = CATALYST_INPUT_DROPPER_POS.north().above();

    @GameTest(template = KlaxonGameTestTemplates.DEEPSLATE_BLAST_PROCESSOR)
    public static void testQuasiconnectivity(KlaxonGameTestHelper helper) {
        helper.assertBlockPresent(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, DEEPSLATE_BLAST_PROCESSOR_POS);
        triggerDeepslateBlastProcessor(helper);
        helper.assertBlockProperty(DEEPSLATE_BLAST_PROCESSOR_POS, DeepslateBlastProcessorBlock.TRIGGERED, Boolean::booleanValue, "Quasiconnectivity failed to trigger! It's an intended feature!");
        helper.succeed();
    }

    @GameTest(template = KlaxonGameTestTemplates.DEEPSLATE_BLAST_PROCESSOR)
    public static void testFastInput(KlaxonGameTestHelper helper) {
        FakePlayer player = helper.createFakePlayer(GameType.SURVIVAL);
        ItemStack gunpowderStack = new ItemStack(Items.GUNPOWDER, 64);
        ItemStack coalStack = new ItemStack(Items.COAL, 64);

        // test top fastinput with survival mode
        player.setItemInHand(InteractionHand.MAIN_HAND, coalStack);
        helper.useBlock(DEEPSLATE_BLAST_PROCESSOR_POS, player, helper.hitResult(DEEPSLATE_BLAST_PROCESSOR_POS, Direction.UP));
        helper.assertContainerContains(DEEPSLATE_BLAST_PROCESSOR_POS, Items.COAL);
        helper.assertBlockProperty(DEEPSLATE_BLAST_PROCESSOR_POS, DeepslateBlastProcessorBlock.LOOT_STATE, DeepslateBlastProcessorLootState.INGREDIENT_ONLY);
        helper.expectInt(63, coalStack.getCount(), "Coal Stack should decrement in Survival Mode!");


        // test side fastinput with creative mode
        player.setGameMode(GameType.CREATIVE);

        player.setItemInHand(InteractionHand.MAIN_HAND, gunpowderStack);
        helper.useBlock(DEEPSLATE_BLAST_PROCESSOR_POS, player, helper.hitResult(DEEPSLATE_BLAST_PROCESSOR_POS, Direction.SOUTH));
        helper.assertContainerContains(DEEPSLATE_BLAST_PROCESSOR_POS, Items.GUNPOWDER);
        helper.assertBlockProperty(DEEPSLATE_BLAST_PROCESSOR_POS, DeepslateBlastProcessorBlock.LOOT_STATE, DeepslateBlastProcessorLootState.FULL);
        helper.expectInt(64, gunpowderStack.getCount(), "Gunpowder Stack should not decrement in Creative Mode!");

        helper.succeed();
    }

    @GameTest(template = KlaxonGameTestTemplates.DEEPSLATE_BLAST_PROCESSOR)
    public static void testAutomationInputAndSmeltingCrafting(KlaxonGameTestHelper helper) {
        setIngredientDropperItem(helper, new ItemStack(Items.GLOWSTONE_DUST));
        setCatalystDropperItem(helper, new ItemStack(Items.RAW_IRON));
        triggerIngredientDropper(helper);
        triggerCatalystDropper(helper);

        // wait for droppers to trigger
        helper.runAfterDelay(4, () -> {
            // check contents
            helper.assertContainerContains(DEEPSLATE_BLAST_PROCESSOR_POS, Items.GLOWSTONE_DUST);
            helper.assertContainerContains(DEEPSLATE_BLAST_PROCESSOR_POS, Items.RAW_IRON);

            // trigger deepslate blast processor
            triggerDeepslateBlastProcessor(helper);

            // wait for deepslate blast processor to trigger and dump items
            helper.runAfterDelay(12, () -> {
                // test final crafting thing
                helper.assertContainerContains(BARREL_POS, KlaxonItems.FRACTURED_IRON.value());
                helper.succeed();
            });
        });
    }

    @GameTest(template = KlaxonGameTestTemplates.DEEPSLATE_BLAST_PROCESSOR)
    public static void testComparatorSignalsAndLootState(KlaxonGameTestHelper helper) {
        // empty
        DeepslateBlastProcessorBlockEntity blockEntity = helper.getBlockEntity(DEEPSLATE_BLAST_PROCESSOR_POS);
        helper.expectInt(0, helper.getAnalogSignal(DEEPSLATE_BLAST_PROCESSOR_POS), "Deepslate Blast Processor should have analog signal strength of 0 when empty");
        helper.assertBlockProperty(DEEPSLATE_BLAST_PROCESSOR_POS, DeepslateBlastProcessorBlock.LOOT_STATE, DeepslateBlastProcessorLootState.EMPTY);

        // catalyst only
        blockEntity.getCatalystPartition().setItem(0, new ItemStack(KlaxonItems.MODULAR_EXPLOSIVE_BLOCK));
        helper.expectInt(7, helper.getAnalogSignal(DEEPSLATE_BLAST_PROCESSOR_POS), "Deepslate Blast Processor should have analog signal strength of 7 when it has only the catalyst");
        helper.assertBlockProperty(DEEPSLATE_BLAST_PROCESSOR_POS, DeepslateBlastProcessorBlock.LOOT_STATE, DeepslateBlastProcessorLootState.CATALYST_ONLY);

        // full
        blockEntity.getIngredientPartition().setItem(0, new ItemStack(KlaxonItems.PRECISION_DISPENSER));
        helper.expectInt(15, helper.getAnalogSignal(DEEPSLATE_BLAST_PROCESSOR_POS), "Deepslate Blast Processor should have analog signal strength of 15 when it has both a catalyst and ingredient");
        helper.assertBlockProperty(DEEPSLATE_BLAST_PROCESSOR_POS, DeepslateBlastProcessorBlock.LOOT_STATE, DeepslateBlastProcessorLootState.FULL);

        // ingredient only
        blockEntity.getCatalystPartition().clearContent();
        helper.expectInt(8, helper.getAnalogSignal(DEEPSLATE_BLAST_PROCESSOR_POS), "Deepslate Blast Processor should have analog signal strength of 15 when it has only an ingredient");
        helper.assertBlockProperty(DEEPSLATE_BLAST_PROCESSOR_POS, DeepslateBlastProcessorBlock.LOOT_STATE, DeepslateBlastProcessorLootState.INGREDIENT_ONLY);

        // unlooted
        blockEntity.clearContent();
        blockEntity.setLootTable(KlaxonLootTables.GERALD_SNIFFER_GAMEPLAY);
        blockEntity.setChanged();
        helper.assertBlockProperty(DEEPSLATE_BLAST_PROCESSOR_POS, DeepslateBlastProcessorBlock.LOOT_STATE, DeepslateBlastProcessorLootState.UNLOOTED);

        helper.succeed();
    }

    private static void setIngredientDropperItem(KlaxonGameTestHelper helper, ItemStack newStack) {
        DropperBlockEntity blockEntity = helper.getBlockEntity(INGREDIENT_INPUT_DROPPER_POS);
        blockEntity.clearContent();
        blockEntity.setItem(0, newStack);
    }

    private static void triggerIngredientDropper(KlaxonGameTestHelper helper) {
        helper.pushButton(INGREDIENT_INPUT_BUTTON_POS);
    }

    private static void setCatalystDropperItem(KlaxonGameTestHelper helper, ItemStack newStack) {
        DropperBlockEntity blockEntity = helper.getBlockEntity(CATALYST_INPUT_DROPPER_POS);
        blockEntity.clearContent();
        blockEntity.setItem(0, newStack);
    }

    private static void triggerCatalystDropper(KlaxonGameTestHelper helper) {
        helper.pushButton(CATALYST_INPUT_BUTTON_POS);
    }

    private static void triggerDeepslateBlastProcessor(KlaxonGameTestHelper helper) {
        helper.pushButton(DEEPSLATE_BLAST_PROCESSOR_TRIGGER_BUTTON_POS);
    }
}
