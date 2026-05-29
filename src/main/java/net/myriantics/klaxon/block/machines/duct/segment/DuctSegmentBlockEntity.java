package net.myriantics.klaxon.block.machines.duct.segment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctNode;
import org.jetbrains.annotations.Nullable;

public class DuctSegmentBlockEntity extends BlockEntity implements DuctNode {

    private NonNullList<ItemStack> stacks;
    private DuctNode last;
    private DuctNode next;

    public DuctSegmentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public boolean advance(Direction advancingDirection) {
        return false;
    }

    @Override
    public @Nullable DuctNode last() {
        return null;
    }

    @Override
    public @Nullable DuctNode next() {
        return null;
    }
}
