package net.myriantics.klaxon.item.block;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;

public class KlaxonDoubleTallBlockItem extends ItemNameBlockItem {

    public KlaxonDoubleTallBlockItem(Block bottomBlock, Properties properties) {
        super(bottomBlock, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult resultOfPlacingBottomAtPos = super.place(context);

        if (resultOfPlacingBottomAtPos == InteractionResult.FAIL) {
            BlockPlaceContext downOffsetContext = this.createDownOffsetContext(context);
            return super.place(downOffsetContext);
        }

        return resultOfPlacingBottomAtPos;
    }

    protected BlockPlaceContext createDownOffsetContext(BlockPlaceContext context) {
        return new BlockPlaceContext(
                context.getLevel(),
                context.getPlayer(),
                context.getHand(),
                context.getItemInHand(),
                new BlockHitResult(
                        context.getClickLocation().subtract(0, 1, 0),
                        context.getClickedFace(),
                        context.getClickedPos().below(),
                        context.isInside()
                )
        );
    }
}
