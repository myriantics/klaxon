package net.myriantics.klaxon.mechanics.wrench.overlays;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.wrench.SelectedFaceCalculator;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.util.BlockFaceRegion;

public final class SelectedWrenchInteractionOverlay extends AbstractWrenchInteractionOverlay {

    private BlockFaceRegion blockFaceRegionCache = null;

    @Override
    public BlockFaceRegion getRegion() {
        return this.blockFaceRegionCache;
    }

    @Override
    public void tick(WrenchActionContext.Manual context) {
        super.tick(context);
        if (!this.validate(context)) {
            this.recomputeRenderRegion(context);
            this.resetCache(context);
        }
    }

    private void recomputeRenderRegion(WrenchActionContext.Manual context) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();
        Direction direction = context.getHitResult().getDirection();

        SelectedFaceCalculator calculator = new SelectedFaceCalculator(direction, context.getClickPosFromCorner().toVector3f());
        state.getShape(level, pos).forAllEdges(calculator::tryAdd);
        this.blockFaceRegionCache = calculator.get();
    }
}
