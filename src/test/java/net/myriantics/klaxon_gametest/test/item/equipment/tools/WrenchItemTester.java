package net.myriantics.klaxon_gametest.test.item.equipment.tools;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon_gametest.util.KlaxonGameTestHelper;

public class WrenchItemTester {
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void testWrenchBlockPickup(KlaxonGameTestHelper context) {
        Vec3 targetPos = Vec3.atCenterOf(BlockPos.ZERO);
        context.setBlock(BlockPos.ZERO, KlaxonBlocks.NETHER_REACTOR_CORE.value().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X));

        Player player = context.makeMockPlayer(GameType.SURVIVAL);
        player.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
        player.getInventory().setItem(0, new ItemStack(KlaxonItems.STEEL_WRENCH));
        player.getInventory().selected = 0;
        context.getLevel().addFreshEntity(player);

        BlockPos blockPos = context.absolutePos(BlockPos.ZERO);
        BlockHitResult blockHitResult = new BlockHitResult(targetPos, Direction.DOWN, blockPos, false);
        UseOnContext itemUsageContext = new UseOnContext(player, InteractionHand.MAIN_HAND, blockHitResult);
        player.getMainHandItem().useOn(itemUsageContext);

        context.assertBlockProperty(BlockPos.ZERO, BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.Z);
        context.succeed();
    }
}
