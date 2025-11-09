package net.myriantics.klaxon.mechanics.grapple_winch.connection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public final class ClientGrappleWinchConnection extends GrappleWinchConnection {

    private Vec3d playerFallbackPos;
    private Vec3d playerFallbackPrevPos;
    private Vec3d hookFallbackPos;
    private Vec3d hookFallbackPrevPos;

    private final int playerId;
    private final int hookId;

    private AbstractClientPlayerEntity player = null;
    private GrapplingHook hook = null;

    public ClientGrappleWinchConnection(int connectionId, int playerId, int hookId) {
        super(connectionId);
        this.playerId = playerId;
        this.hookId = hookId;
    }

    public ClientGrappleWinchConnection(GrappleWinchConnectionSyncPacket packet) {
        this(packet.connectionId(), packet.playerId(), packet.hookId());
        this.playerFallbackPos = packet.playerFallbackPos();
        this.hookFallbackPos = packet.hookFallbackPos();
        this.hookAnchored = packet.hookAnchored();
    }

    public void sync(GrappleWinchConnectionSyncPacket packet) {
        this.playerFallbackPrevPos = this.playerFallbackPos;
        this.playerFallbackPos = packet.playerFallbackPos();
        this.hookFallbackPrevPos = this.hookFallbackPos;
        this.hookFallbackPos = packet.hookFallbackPos();
        this.hookAnchored = packet.hookAnchored();
        if (this.player != null && !this.player.equals(MinecraftClient.getInstance().player)) {
            this.cableLength = packet.cableLength();
        }
    }

    @Override
    public void tick() {
        this.retracting = player.isUsingItem() && player.getActiveItem().isOf(KlaxonItems.GRAPPLE_WINCH);

        super.tick();

        // only do movement stuff if we're the client main player
        if (this.player.equals(MinecraftClient.getInstance().player)) {
            Vec3d compiledPlayerVec = Vec3d.ZERO;

            double maxWinchCableLength = player.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);

            // initialize values
            Vec3d playerToHookVec = this.getHookPos().subtract(this.getPlayerPos().add(0, player.getEyeY(), 0));
            double clawDistance = playerToHookVec.length();

            // update winch cable length
            if (this.retracting || (this.player.isOnGround() && clawDistance > this.cableLength)) {
                this.cableLength = clawDistance;
            }

            // make sure grapple claw is loaded and anchored
            if (isHookAnchored() && !EntityWeightHelper.isHeavy(this.player)) {

                // get movement vectors and normalize them
                playerToHookVec = playerToHookVec.normalize();
                Vec3d playerFacingVec = this.player.getRotationVec(1.0f).normalize();

                // tick retraction movement
                if (this.retracting) {

                    // transform movement vectors
                    Vec3d playerToClawRetractionVec = playerToHookVec.multiply(2./20);
                    // player can direct movement with facing direction to combat getting stuck under ledges
                    Vec3d playerFacingRetractionVec = playerFacingVec.multiply(1./20).multiply(this.player.isSprinting() ? 1.5 : 1);

                    // add vectors to self vector
                    if (!this.player.isSneaking()) {
                        compiledPlayerVec = compiledPlayerVec.add(playerToClawRetractionVec).add(playerFacingRetractionVec);
                    }
                }

                // apply velocity to player if they go past target range
                // retraction is only capped at the max range
                // cable length is also less regulated when sneaking & retracting so that players can descend with the grapple winch
                if (clawDistance > ((this.player.isSneaking() && this.retracting) || this.player.isOnGround()
                        ? maxWinchCableLength
                        : Math.min(maxWinchCableLength, cableLength)
                )) {
                    Vec3d playerRangeCorrectionVec = playerToHookVec.multiply(0.1);
                    playerRangeCorrectionVec = playerRangeCorrectionVec.add(0, player.getFinalGravity(), 0);
                    compiledPlayerVec = compiledPlayerVec.add(playerRangeCorrectionVec);
                }
            }

            // commit velocity
            player.addVelocity(compiledPlayerVec);
        }
    }

    public boolean validate() {
        return canPlayerSupportGrappleWinchCable();
    }

    private boolean canPlayerSupportGrappleWinchCable() {
        return stackSupportsGrappleWinchCable(this.player.getMainHandStack()) || stackSupportsGrappleWinchCable(this.player.getOffHandStack());
    }

    private boolean stackSupportsGrappleWinchCable(ItemStack stack) {
        return stack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(stack);
    }

    @Override
    public int getPlayerId() {
        return playerId;
    }

    @Override
    public int getHookId() {
        return hookId;
    }

    @Override
    public PlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public GrapplingHook getHook() {
        return this.hook;
    }

    @Override
    public Vec3d getHookPos() {
        return this.hook.klaxon$asEntity().isRemoved() ? this.hookFallbackPos : this.hook.klaxon$asEntity().getPos();
    }

    @Override
    public Vec3d getPlayerPos() {
        return this.player.isRemoved() ? this.playerFallbackPos : this.player.getPos();
    }

    public Vec3d getLerpedPlayerPos(float delta) {
        if (this.player != null && !this.player.isRemoved()) {
            return this.player.getLerpedPos(delta);
        }

        double lerpedX = MathHelper.lerp(delta, this.playerFallbackPrevPos.getX(), this.playerFallbackPos.getX());
        double lerpedY = MathHelper.lerp(delta, this.playerFallbackPrevPos.getY(), this.playerFallbackPos.getY());
        double lerpedZ = MathHelper.lerp(delta, this.playerFallbackPrevPos.getZ(), this.playerFallbackPos.getZ());
        return new Vec3d(lerpedX, lerpedY, lerpedZ);
    }

    public Vec3d getLerpedHookPos(float delta) {
        if (this.hook != null && !this.hook.klaxon$asEntity().isRemoved()) {
            return this.hook.klaxon$asEntity().getLerpedPos(delta);
        }

        double lerpedX = MathHelper.lerp(delta, this.hookFallbackPrevPos.getX(), this.hookFallbackPos.getX());
        double lerpedY = MathHelper.lerp(delta, this.hookFallbackPrevPos.getY(), this.hookFallbackPos.getY());
        double lerpedZ = MathHelper.lerp(delta, this.hookFallbackPrevPos.getZ(), this.hookFallbackPos.getZ());
        return new Vec3d(lerpedX, lerpedY, lerpedZ);
    }

    @Override
    public void resetCableLength() {
        this.cableLength = this.player.getEyePos().distanceTo(this.hook.klaxon$asEntity().getPos());
    }
}
