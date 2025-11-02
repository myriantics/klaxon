package net.myriantics.klaxon_gametest.test.item.equipment.tools;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon_gametest.KlaxonTestContext;

public class WrenchItemTester {
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWrenchBlockPickup(KlaxonTestContext context) {
        Vec3d targetPos = Vec3d.ofCenter(BlockPos.ZERO);
        context.setBlockState(BlockPos.ORIGIN, KlaxonBlocks.NETHER_REACTOR_CORE.getDefaultState().with(Properties.HORIZONTAL_AXIS, Direction.Axis.X));

        PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
        player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, targetPos);
        player.getInventory().setStack(0, new ItemStack(KlaxonItems.STEEL_WRENCH));
        player.getInventory().selectedSlot = 0;
        context.getWorld().spawnEntity(player);

        BlockPos blockPos = context.getAbsolutePos(BlockPos.ORIGIN);
        BlockHitResult blockHitResult = new BlockHitResult(targetPos, Direction.DOWN, blockPos, false);
        ItemUsageContext itemUsageContext = new ItemUsageContext(player, Hand.MAIN_HAND, blockHitResult);
        player.getMainHandStack().useOnBlock(itemUsageContext);

        context.expectBlockProperty(BlockPos.ORIGIN, Properties.HORIZONTAL_AXIS, Direction.Axis.Z);
        context.complete();
    }
}
