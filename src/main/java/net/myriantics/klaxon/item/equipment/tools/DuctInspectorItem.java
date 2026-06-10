package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctNode;
import net.myriantics.klaxon.mechanics.logistics.itemduct.IDuctNodeBlock;
import org.jetbrains.annotations.Nullable;

public class DuctInspectorItem extends Item {
    public DuctInspectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos targetPos = context.getClickedPos();
        BlockState targetState = level.getBlockState(targetPos);
        @Nullable Player player = context.getPlayer();

        if (player instanceof ServerPlayer serverPlayer && targetState.getBlock() instanceof IDuctNodeBlock nodeBlock) {
            @Nullable DuctNode node = nodeBlock.getNode(level, targetPos, targetState, level.getBlockEntity(targetPos));
            if (node != null) {
                serverPlayer.displayClientMessage(Component.literal("Displaying node status for [").append(targetState.getBlock().getName()).append("] at " + targetPos), false);
                for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                    serverPlayer.displayClientMessage(Component.literal(direction.toString() + ": [" + node.getStatusForDirection(direction) + "]"), false);
                }
            }
        }

        return super.useOn(context);
    }
}
