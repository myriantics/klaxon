package net.myriantics.klaxon_gametest.test.block.machines.blast_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.phys.AABB;
import net.myriantics.klaxon.block.machines.blast_processor.steel.SteelBlastProcessorBlockEntity;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.util.container.ContainerPartition;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestHelper;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestTemplates;

public class SteelBlastProcessorBlockTester {

    private static final BlockPos ORIGIN = BlockPos.ZERO.above();
    private static final BlockPos STEEL_BLAST_PROCESSOR_POS = ORIGIN.above().east().south();
    private static final BlockPos STEEL_BLAST_PROCESSOR_EXHAUST_POS = STEEL_BLAST_PROCESSOR_POS.above();
    private static final BlockPos OUTPUT_BARREL_POS = STEEL_BLAST_PROCESSOR_POS.east();
    private static final BlockPos STEEL_BLAST_PROCESSOR_TRIGGER_BUTTON_POS = OUTPUT_BARREL_POS.above();
    private static final BlockPos INGREDIENT_INPUT_DROPPER_POS = STEEL_BLAST_PROCESSOR_POS.north();
    private static final BlockPos INGREDIENT_INPUT_DROPPER_TRIGGER_BUTTON_POS = INGREDIENT_INPUT_DROPPER_POS.east().above();
    private static final BlockPos CATALYST_INPUT_DROPPER_POS = STEEL_BLAST_PROCESSOR_POS.west();
    private static final BlockPos CATALYST_INPUT_DROPPER_TRIGGER_BUTTON_POS = CATALYST_INPUT_DROPPER_POS.south().above();

    private static final BlockPos DRAGON_EGG_STEEL_BLAST_PROCESSOR_POS = ORIGIN.east(16).south(16).above(7);
    private static final BlockPos DRAGON_EGG_STEEL_BLAST_PROCESSOR_TRIGGER_LEVER_POS = DRAGON_EGG_STEEL_BLAST_PROCESSOR_POS.west();
    private static final BlockPos DRAGON_EGG_EXHAUST_POS = DRAGON_EGG_STEEL_BLAST_PROCESSOR_POS.above();

    @GameTest(template = KlaxonGameTestTemplates.STEEL_BLAST_PROCESSOR)
    public static void testExhaustLaunch(KlaxonGameTestHelper helper) {
        SteelBlastProcessorBlockEntity blockEntity = helper.getBlockEntity(STEEL_BLAST_PROCESSOR_POS);
        blockEntity.getCatalystPartition().setItem(0, new ItemStack(Items.END_CRYSTAL));

        Villager villager = helper.spawn(EntityType.VILLAGER, STEEL_BLAST_PROCESSOR_EXHAUST_POS);
        villager.setCustomName(Component.literal("test dummy"));

        triggerSteelBlastProcessor(helper);
        helper.runAfterDelay(5, () -> {
            helper.assertContainerEmpty(STEEL_BLAST_PROCESSOR_POS);
            helper.assertBlockPresent(Blocks.FIRE, STEEL_BLAST_PROCESSOR_EXHAUST_POS);
            helper.expectBoolean(true, villager.getDeltaMovement().y() > 0, "Test Dummy Villager should have upwards velocity!");
            helper.expectBoolean(true, villager.getHealth() < villager.getMaxHealth(), "Test Dummy Villager should be damaged!");
            helper.expectBoolean(true, villager.isOnFire(), "Test Dummy Villager should be on fire!");
            villager.discard();

            helper.succeed();
        });
    }

    @GameTest(template = KlaxonGameTestTemplates.STEEL_BLAST_PROCESSOR)
    public static void testCraftEjectionIntoBlockContainer(KlaxonGameTestHelper helper) {
        SteelBlastProcessorBlockEntity blockEntity = helper.getBlockEntity(STEEL_BLAST_PROCESSOR_POS);
        blockEntity.getCatalystPartition().setItem(0, new ItemStack(Items.GLOWSTONE_DUST));
        blockEntity.getIngredientPartition().setItem(0, new ItemStack(Items.RAW_IRON, 3));

        triggerSteelBlastProcessor(helper);
        helper.runAfterDelay(5, () -> {
            BarrelBlockEntity barrelBlockEntity = helper.getBlockEntity(OUTPUT_BARREL_POS);
            ItemStack outputFragmentStack = barrelBlockEntity.getItem(0);
            helper.expectBoolean(true, outputFragmentStack.is(KlaxonItems.FRACTURED_IRON) && outputFragmentStack.getCount() >= 3, "Output Barrel does not have at least the 3 expected Fractured Iron!");
            helper.succeed();
        });
    }

    @GameTest(template = KlaxonGameTestTemplates.STEEL_BLAST_PROCESSOR)
    public static void testComparatorOutputs(KlaxonGameTestHelper helper) {
        SteelBlastProcessorBlockEntity blockEntity = helper.getBlockEntity(STEEL_BLAST_PROCESSOR_POS);
        ContainerPartition catalystPartition = blockEntity.getCatalystPartition();
        ContainerPartition ingredientPartition = blockEntity.getIngredientPartition();
        helper.expectInt(0, helper.getAnalogSignal(STEEL_BLAST_PROCESSOR_POS), "Steel Blast Processor should have a signal strength of 0 when empty");
        for (int i = 1; i < 5; i++) {
            blockEntity.clearContent();
            ingredientPartition.setItem(0, new ItemStack(Items.RAW_IRON, i));
            helper.expectInt(
                    i * 2,
                    helper.getAnalogSignal(STEEL_BLAST_PROCESSOR_POS),
                    "Steel Blast Processor should have a signal strength of [" + i * 2 + "] with no catalyst and [" + i + "] ingredients"
            );
            catalystPartition.setItem(0, new ItemStack(Items.COAL));
            helper.expectInt(
                    i * 2 + 7,
                    helper.getAnalogSignal(STEEL_BLAST_PROCESSOR_POS),
                    "Steel Blast Processor should have a signal strength of [" + i * 2 + 7 + "] with catalyst present and [" + i + "] ingredients"
            );
        }
        helper.succeed();
    }

    @GameTest(template = KlaxonGameTestTemplates.STEEL_BLAST_PROCESSOR)
    public static void testCampfireFireHolder(KlaxonGameTestHelper helper) {
        SteelBlastProcessorBlockEntity blockEntity = helper.getBlockEntity(STEEL_BLAST_PROCESSOR_POS);
        blockEntity.getCatalystPartition().setItem(0, new ItemStack(Items.END_CRYSTAL));
        helper.setBlock(STEEL_BLAST_PROCESSOR_EXHAUST_POS, Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, false));

        triggerSteelBlastProcessor(helper);
        helper.runAfterDelay(5, () -> {
            helper.assertBlockPresent(KlaxonBlocks.STEEL_BLAST_PROCESSOR, STEEL_BLAST_PROCESSOR_POS);
            helper.assertBlockPresent(Blocks.CAMPFIRE, STEEL_BLAST_PROCESSOR_EXHAUST_POS);
            helper.assertBlockProperty(STEEL_BLAST_PROCESSOR_EXHAUST_POS, CampfireBlock.LIT, Boolean::booleanValue, "Campfire should be lit after detonation");
            helper.succeed();
        });
    }

    @GameTest(template = KlaxonGameTestTemplates.STEEL_BLAST_PROCESSOR)
    public static void testWaterloggedCampfireFireHolder(KlaxonGameTestHelper helper) {
        SteelBlastProcessorBlockEntity blockEntity = helper.getBlockEntity(STEEL_BLAST_PROCESSOR_POS);
        blockEntity.getCatalystPartition().setItem(0, new ItemStack(Items.END_CRYSTAL));
        helper.setBlock(STEEL_BLAST_PROCESSOR_EXHAUST_POS, Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, false).setValue(CampfireBlock.WATERLOGGED, true));

        triggerSteelBlastProcessor(helper);
        helper.runAfterDelay(4, () -> {
            helper.assertBlockNotPresent(KlaxonBlocks.STEEL_BLAST_PROCESSOR, STEEL_BLAST_PROCESSOR_POS);
            helper.succeed();
        });
    }

    @GameTest(template = KlaxonGameTestTemplates.STEEL_BLAST_PROCESSOR_DRAGON_EGG_MOVEMENT)
    public static void testDragonEggSchmovement(KlaxonGameTestHelper helper) {
        SteelBlastProcessorBlockEntity blockEntity = helper.getBlockEntity(DRAGON_EGG_STEEL_BLAST_PROCESSOR_POS);
        blockEntity.getCatalystPartition().setItem(0, new ItemStack(Items.END_CRYSTAL));
        helper.setBlock(DRAGON_EGG_EXHAUST_POS, Blocks.DRAGON_EGG);

        helper.pullLever(DRAGON_EGG_STEEL_BLAST_PROCESSOR_TRIGGER_LEVER_POS);
        helper.runAfterDelay(10, () -> {
            helper.assertBlockPresent(KlaxonBlocks.STEEL_BLAST_PROCESSOR, DRAGON_EGG_STEEL_BLAST_PROCESSOR_POS);
            boolean found = false;
            for (FallingBlockEntity entity : helper.getEntities(EntityType.FALLING_BLOCK)) {
                if (entity.getBlockState().is(Blocks.DRAGON_EGG)) {
                    found = true;
                    entity.discard();
                    break;
                }
            }

            helper.expectBoolean(true, found, "No Dragon Egg Falling Blocks found in radius!");
            helper.assertBlockPresent(Blocks.FIRE, DRAGON_EGG_EXHAUST_POS);
            helper.succeed();
        });
    }

    @GameTest(template = KlaxonGameTestTemplates.STEEL_BLAST_PROCESSOR)
    public static void testExhaustTntIgnition(KlaxonGameTestHelper helper) {
        SteelBlastProcessorBlockEntity blockEntity = helper.getBlockEntity(STEEL_BLAST_PROCESSOR_POS);
        blockEntity.getCatalystPartition().setItem(0, new ItemStack(Items.END_CRYSTAL));

        helper.setBlock(STEEL_BLAST_PROCESSOR_EXHAUST_POS, Blocks.TNT);
        triggerSteelBlastProcessor(helper);

        helper.runAfterDelay(4, () -> {
            helper.assertBlockPresent(Blocks.FIRE, STEEL_BLAST_PROCESSOR_EXHAUST_POS);
            helper.assertBlockPresent(KlaxonBlocks.STEEL_BLAST_PROCESSOR, STEEL_BLAST_PROCESSOR_POS);
            helper.assertEntityPresent(EntityType.TNT, STEEL_BLAST_PROCESSOR_EXHAUST_POS);
            helper.getEntities(EntityType.TNT, STEEL_BLAST_PROCESSOR_EXHAUST_POS, 1).forEach(Entity::discard);
            helper.succeed();
        });
    }

    private static void setIngredientDropperItem(KlaxonGameTestHelper helper, ItemStack newStack) {
        DropperBlockEntity blockEntity = helper.getBlockEntity(INGREDIENT_INPUT_DROPPER_POS);
        blockEntity.clearContent();
        blockEntity.setItem(0, newStack);
    }

    private static void triggerIngredientDropper(KlaxonGameTestHelper helper) {
        helper.pushButton(INGREDIENT_INPUT_DROPPER_TRIGGER_BUTTON_POS);
    }

    private static void setCatalystDropperItem(KlaxonGameTestHelper helper, ItemStack newStack) {
        DropperBlockEntity blockEntity = helper.getBlockEntity(CATALYST_INPUT_DROPPER_POS);
        blockEntity.clearContent();
        blockEntity.setItem(0, newStack);
    }

    private static void triggerCatalystDropper(KlaxonGameTestHelper helper) {
        helper.pushButton(CATALYST_INPUT_DROPPER_TRIGGER_BUTTON_POS);
    }

    private static void triggerSteelBlastProcessor(KlaxonGameTestHelper helper) {
        helper.pushButton(STEEL_BLAST_PROCESSOR_TRIGGER_BUTTON_POS);
    }
}
