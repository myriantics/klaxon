package net.myriantics.klaxon.entity.entities.grapple_claw;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchItem;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchNetworkUtil;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import org.jetbrains.annotations.NotNull;

public class GrappleClawCableAttachmentHandler {
    private final GrappleClawEntity grappleClaw;

    private AttachmentState attachmentState = AttachmentState.DETACHED;

    public GrappleClawCableAttachmentHandler(GrappleClawEntity grappleClaw) {
        this.grappleClaw = grappleClaw;
    }

    protected void tick(World world, @NotNull PlayerEntity attachedPlayer, boolean retracting) {
        this.detachCableIfInvalid();

        Vec3d compiledVec = Vec3d.ZERO;

        Vec3d clawEyePos = grappleClaw.getPos();
        Vec3d attachedEyePos = attachedPlayer.getEyePos();
        Vec3d normalizedClaw2WielderVec = attachedEyePos.subtract(clawEyePos).normalize();

        double ownerDistance = clawEyePos.distanceTo(attachedEyePos);
        double currentWinchCableLength = attachedPlayer.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);

        // limit fall distance to give players more leeway
        if (attachedPlayer.getVelocity().getY() > -1 && attachedPlayer.fallDistance > 1.0F) {
            attachedPlayer.fallDistance = 1.0F;
        }

        // if the attached player is heavy and retracting, de-anchor and pop advancement if succeeded
        if (!world.isClient() && retracting && EntityWeightHelper.isHeavy(attachedPlayer)) {
            this.grappleClaw.deAnchorIfPossible(normalizedClaw2WielderVec);
        }

        // owner being heavy overrides anchoring
        if (!grappleClaw.isAnchored()) {

            // retract grapple claw if owner pulls back before landing
            if (retracting) {
                compiledVec = compiledVec.add(normalizedClaw2WielderVec.multiply(4f/20));
            }

            // retract grapple claw if it hits limit
            if (ownerDistance >= currentWinchCableLength) {
                compiledVec = compiledVec.add(normalizedClaw2WielderVec.multiply(4f/20));

                grappleClaw.playSoundAtSelfAndThroughCableIfPossible(
                        KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_REBOUND_AT_LIMIT,
                        1f + world.getRandom().nextFloat() * 0.3f,
                        0.8f + world.getRandom().nextFloat() * 0.2f
                );
                world.emitGameEvent(
                        GameEvent.ENTITY_ACTION,
                        clawEyePos,
                        GameEvent.Emitter.of(attachedPlayer)
                );
            }
        }

        // commit the total velocity edits to self or whatever entity we're attached to
        grappleClaw.hookedEntityHandler.tick(compiledVec);
    }

    /**
     * Detaches cable if attached and owner is no longer holding a Grapple Winch <br>
     * Also detaches if player is removed, dead, too far away, or in a different dimension. <br>
     * Called every tick.
     */
    private void detachCableIfInvalid() {
        if (this.grappleClaw.getAttachedPlayer() instanceof ServerPlayerEntity serverPlayer) {
            ItemStack itemStack = serverPlayer.getMainHandStack();
            ItemStack itemStack2 = serverPlayer.getOffHandStack();
            boolean bl = itemStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(itemStack);
            boolean bl2 = itemStack2.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(itemStack2);

            boolean cableTooLong = this.grappleClaw.getPos().distanceTo(serverPlayer.getEyePos()) > serverPlayer.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH) * 1.5f;

            // pop advancement if player intentionally disconnected grapple winch cable
            if (!bl && !bl2) {
                KlaxonAdvancementTriggers.triggerGrappleWinchIntentionallyDisconnectCable(serverPlayer);
            }

            if ((!bl && !bl2) || serverPlayer.isRemoved() || serverPlayer.isSpectator() || !serverPlayer.isAlive() || !serverPlayer.getWorld().equals(this.grappleClaw.getWorld()) || cableTooLong) {
                this.detachCable(false);
            }
        }
    }

    public boolean isCableAttached() {
        return this.attachmentState.isAttached();
    }

    /**
     * Only does stuff when {@link GrappleClawEntity#isCableAttached} is true. When run, updates attached to false. <br>
     * Sets owner's grapple claw to null if owner's active grapple claw is this one. <br>
     * Sends packet to client indicating detachment
     */
    public boolean detachCable(boolean silent) {
        PlayerEntity attachedPlayer = this.grappleClaw.getAttachedPlayer();

        if (attachedPlayer != null) {
            if (!silent) {
                // play sound before detaching so we know where to direct the sound
                this.grappleClaw.playSoundAtSelfAndThroughCableIfPossible(
                        KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_DETACH,
                        0.8f + this.grappleClaw.getWorld().getRandom().nextFloat() * 0.2f,
                        0.7f + this.grappleClaw.getWorld().getRandom().nextFloat() * 0.3f
                );
            }

            this.setAttachmentState(AttachmentState.DETACHED);
            ((PlayerEntityGrappleAccess) attachedPlayer).klaxon$setGrappleClaw(null);

            if (attachedPlayer instanceof ServerPlayerEntity serverPlayer) {
                GrappleWinchNetworkUtil.clearFromClients(serverPlayer, this.grappleClaw);
            }

            this.grappleClaw.hookedEntityHandler.releaseHookedEntity();
        }

        return false;
    }

    /**
     * Sets the provided player's Grapple Claw to itself, updates attached variable, sets owner to the provided player, and sends out needed update packets.
     * @param serverPlayer
     * The player to form a cable connection to.
     * @return
     * Returns false if attachment failed - (if claw is removed or already attached to player)
     */
    public boolean attachCable(ServerPlayerEntity serverPlayer) {
        if (!this.grappleClaw.isRemoved()) {
            // make sure we're not reattaching
            if (!this.grappleClaw.isAttachedToPlayer(serverPlayer)) {
                ((PlayerEntityGrappleAccess) serverPlayer).klaxon$setGrappleClaw(this.grappleClaw);
                this.grappleClaw.setOwner(serverPlayer);
                this.setAttachmentState(AttachmentState.ATTACHED);
                GrappleWinchNetworkUtil.syncToClients(serverPlayer, this.grappleClaw);
                return true;
            }
        }
        return false;
    }

    public void setAttachmentState(AttachmentState newState) {
        this.attachmentState = newState;
    }

    public enum AttachmentState {
        ATTACHED,
        DETACHED;

        public boolean isAttached() {
            return this.equals(ATTACHED);
        }
    }
}
