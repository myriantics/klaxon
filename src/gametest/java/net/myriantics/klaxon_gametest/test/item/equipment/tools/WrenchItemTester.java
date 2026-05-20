package net.myriantics.klaxon_gametest.test.item.equipment.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestHelper;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestTemplates;

import java.util.Iterator;

public class WrenchItemTester {

    private static final BlockPos TARGET_POSITION = BlockPos.ZERO.above();

    @GameTest(template = KlaxonGameTestTemplates.EMPTY_1X1)
    public void testBasicWrenchBlockPickup(KlaxonGameTestHelper helper) {
        helper.setBlock(TARGET_POSITION, KlaxonBlocks.NETHER_REACTOR_CORE);

        InteractionHand hand = InteractionHand.MAIN_HAND;
        Player player = this.initPlayer(helper, GameType.SURVIVAL, hand, true);
        ItemStack wrenchStack = player.getItemInHand(hand);

        BlockHitResult blockHitResult = helper.hitResult(TARGET_POSITION);
        UseOnContext itemUsageContext = new UseOnContext(player, hand, blockHitResult);
        wrenchStack.useOn(itemUsageContext);

        helper.assertBlockNotPresent(KlaxonBlocks.NETHER_REACTOR_CORE, TARGET_POSITION);
        this.testDura(helper, wrenchStack);
        helper.succeed();

        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(KlaxonItems.NETHER_REACTOR_CORE)) {
                helper.succeed();
                return;
            }
        }

        helper.fail("Could not find pickup stack in player inventory after pickup!");
    }

    @GameTest(template = KlaxonGameTestTemplates.EMPTY_1X1)
    public void testContainerWrenchBlockPickup(KlaxonGameTestHelper helper) {
        helper.setBlock(TARGET_POSITION, Blocks.SHULKER_BOX);

        ShulkerBoxBlockEntity blockEntity = helper.getBlockEntity(TARGET_POSITION);
        blockEntity.setItem(0, new ItemStack(KlaxonItems.GRAPPLE_WINCH));
        blockEntity.setItem(1, new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW));

        InteractionHand hand = InteractionHand.MAIN_HAND;
        Player player = this.initPlayer(helper, GameType.SURVIVAL, hand, true);
        ItemStack wrenchStack = player.getItemInHand(hand);

        BlockHitResult hitResult = helper.hitResult(TARGET_POSITION);
        UseOnContext context = new UseOnContext(player, hand, hitResult);
        wrenchStack.useOn(context);

        helper.assertBlockNotPresent(Blocks.SHULKER_BOX, TARGET_POSITION);
        this.testDura(helper, wrenchStack);

        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.SHULKER_BOX)) {
                if (stack.getComponents().get(DataComponents.CONTAINER) instanceof ItemContainerContents contents) {
                    Iterator<ItemStack> iterator = contents.nonEmptyItems().iterator();
                    if (iterator.hasNext() && iterator.next().is(KlaxonItems.GRAPPLE_WINCH) && iterator.hasNext() && iterator.next().is(KlaxonItems.STEEL_GRAPPLE_CLAW)) {
                        helper.succeed();
                    } else {
                        helper.fail("Picked up Shulker Box did not have proper contents!");
                    }
                    return;
                }
            }
        }

        helper.fail("Could not find pickup stack in player inventory after pickup!");
    }

    private Player initPlayer(KlaxonGameTestHelper helper, GameType gameType, InteractionHand hand, boolean sneaking) {
        Player player = helper.makeMockPlayer(gameType);
        player.setShiftKeyDown(sneaking);

        ItemStack wrenchStack = new ItemStack(KlaxonItems.STEEL_WRENCH);
        player.setItemInHand(hand, wrenchStack);
        return player;
    }

    private void testDura(KlaxonGameTestHelper helper, ItemStack wrenchStack) {
        helper.expectInt(1, wrenchStack.getDamageValue(), "Steel Wrench was not properly damaged from pickup test!!");
    }
}
