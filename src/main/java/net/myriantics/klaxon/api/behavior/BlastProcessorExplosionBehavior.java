package net.myriantics.klaxon.api.behavior;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class BlastProcessorExplosionBehavior extends ExplosionBehavior {

    boolean canModifyWorld;

    public BlastProcessorExplosionBehavior(boolean canModifyWorld) {
        super();
        this.canModifyWorld = canModifyWorld;
    }

    @Override
    public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
        return canModifyWorld && super.canDestroyBlock(explosion, world, pos, state, power);
    }
}
