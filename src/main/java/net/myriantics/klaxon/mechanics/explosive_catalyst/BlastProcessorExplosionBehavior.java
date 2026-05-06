package net.myriantics.klaxon.mechanics.explosive_catalyst;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;

public class BlastProcessorExplosionBehavior extends ExplosionDamageCalculator {
    boolean canModifyWorld;

    public BlastProcessorExplosionBehavior(boolean canModifyWorld) {
        super();
        this.canModifyWorld = canModifyWorld;
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float power) {
        return canModifyWorld && super.shouldBlockExplode(explosion, world, pos, state, power);
    }
}
