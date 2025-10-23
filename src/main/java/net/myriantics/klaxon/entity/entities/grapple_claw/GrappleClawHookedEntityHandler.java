package net.myriantics.klaxon.entity.entities.grapple_claw;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw.EnderDragonEntityAccessor;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GrappleClawHookedEntityHandler {

    private final GrappleClawEntity grappleClaw;

    private State state = State.NOT_GRAPPLING;
    private Entity hookedEntity = null;

    protected GrappleClawHookedEntityHandler(GrappleClawEntity grappleClaw) {
        this.grappleClaw = grappleClaw;
    }

    public void onTrackedDataSet(TrackedData<?> data) {
        if (GrappleClawEntity.HOOKED_ENTITY_ID.equals(data)) {
            int i = this.grappleClaw.getDataTracker().get(GrappleClawEntity.HOOKED_ENTITY_ID);

            // id is offset by one to allow for entities with an id of 0
            // was initially confused by this when i saw it in the fishing bobber entity so im dropping this explanation here for myself or whatever future person reads this
            if (i < 0) {
                this.setHookedEntity(null);
            } else {
                Entity entity = this.grappleClaw.getWorld().getEntityById(i - 1);
                switch (entity) {
                    case EnderDragonEntityAccessor access -> {
                        this.setHookedEntity(access.getBody());
                    }
                    case null, default -> {
                        this.setHookedEntity(entity);
                    }
                }
            }
        }
    }

    public void tick(Vec3d compiledVec) {

        // clear grappled entity if it was removed
        if (hookedEntity != null) {
            if (hookedEntity.isRemoved()) {
                this.releaseHookedEntity();
            } else {
                hookedEntity.limitFallDistance();
            }
        }

        switch (this.hookedEntity) {
            case PlayerEntity player -> {
                if (player instanceof ClientPlayerEntity) {
                    player.addVelocity(compiledVec);
                }
            }
            case null -> {
                if (!this.grappleClaw.getWorld().isClient()) {
                    this.grappleClaw.addVelocity(compiledVec);
                }
            }
            default -> {
                this.hookedEntity.addVelocity(compiledVec);
            }
        }
    }

    public boolean isHookedEntityHeavy() {
        return this.hasHookedEntity() && EntityWeightHelper.isHeavy(this.hookedEntity);
    }

    protected boolean canHookEntity(Entity entity) {
        if (entity == null || this.hasHookedEntity()) {
            return false;
        }

        return !entity.getType().isIn(KlaxonEntityTypeTags.GRAPPLE_CLAW_HOOKING_DENYLIST);
    }

    public boolean hookedEntityMatches(Entity entity) {
        return this.hookedEntity == entity;
    }

    public void setHookedEntity(@Nullable Entity entity) {
        this.hookedEntity = entity instanceof EnderDragonPart part
                ? ((EnderDragonEntityAccessor) part.owner).getBody()
                : entity;
        DataTracker dataTracker = this.grappleClaw.getDataTracker();

        this.updateState(entity == null ? State.NOT_GRAPPLING : State.GRAPPLING);

        if (!this.grappleClaw.getWorld().isClient()) {
            // id is offset by one to allow for entities with an id of 0
            // was initially confused by this when i saw it in the fishing bobber entity so im dropping this explanation here for myself or whatever future person reads this
            switch (entity) {
                case EnderDragonPart part -> {
                    dataTracker.set(GrappleClawEntity.HOOKED_ENTITY_ID, part.owner.getId() + 1);
                }
                case null -> {
                    dataTracker.set(GrappleClawEntity.HOOKED_ENTITY_ID, 0);
                }
                default -> {
                    dataTracker.set(GrappleClawEntity.HOOKED_ENTITY_ID, entity.getId() + 1);
                }
            }
        }
    }

    protected void hookEntity(@NotNull Entity entity) {
        if (!this.canHookEntity(entity)) {
            return;
        }

        // update position and velocity
        this.snapClawToHookPos(entity);

        // hook onto entity
        this.setHookedEntity(entity);

        // pop advancement
        if (this.grappleClaw.getAttachedPlayer() instanceof ServerPlayerEntity serverPlayer) {
            KlaxonAdvancementTriggers.triggerEntityGrapple(serverPlayer, entity);
        }

        grappleClaw.playSoundAtSelfAndThroughCableIfPossible(
                KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_ANCHOR,
                1.0F,
                1.0F / (this.grappleClaw.getWorld().getRandom().nextFloat() * 0.4F + 1.2F)
        );

        this.grappleClaw.draggedItems.clear();
    }

    protected void snapClawToHookPos() {
        this.snapClawToHookPos(this.hookedEntity);
    }

    protected void snapClawToHookPos(@NotNull Entity entity) {
        Vec3d targetPos = entity instanceof EnderDragonPart
                ? entity.getPos().add(0, entity.getHeight() / 2, 0)
                : entity.getEyePos();
        this.grappleClaw.setVelocity(Vec3d.ZERO);
        this.grappleClaw.setPosition(targetPos.subtract(0, this.grappleClaw.getHeight() / 2, 0));
    }

    protected void releaseHookedEntity() {
        if (this.hasHookedEntity()) {
            this.grappleClaw.setVelocity(hookedEntity.getVelocity());
            this.setHookedEntity(null);
        }
    }

    public boolean hasHookedEntity() {
        return this.state.isGrappling();
    }

    private void updateState(State newState) {
        this.state = newState;
    }

    public enum State {
        GRAPPLING,
        NOT_GRAPPLING;

        public boolean isGrappling() {
            return this.equals(GRAPPLING);
        }
    }
}
