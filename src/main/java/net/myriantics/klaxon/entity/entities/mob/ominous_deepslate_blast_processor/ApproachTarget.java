package net.myriantics.klaxon.entity.entities.mob.ominous_deepslate_blast_processor;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;

class ApproachTarget extends Goal {

    private OminousDeepslateBlastProcessorEntity mob;
    private PathNavigation navigation;
    private int timeToRecalcPath;

    public ApproachTarget(OminousDeepslateBlastProcessorEntity mob) {
        this.mob = mob;
        this.navigation = mob.getNavigation();
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.navigation.isDone() && this.mob.getTarget() != null && this.mob.position().distanceTo(this.mob.getTarget().position()) > 8;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.mob.getMoveControl().clear();
    }

    @Override
    public void stop() {
        this.navigation.stop();
        this.mob.getMoveControl().clear();
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            Vec3 targetPos = this.mob.getTarget().position();
            this.mob.getMoveControl().setWantedPosition(targetPos.x, targetPos.y, targetPos.z, 1.2);
            // this.navigation.moveTo(this.mob.getTarget(), 0.5);
        }
    }
}
