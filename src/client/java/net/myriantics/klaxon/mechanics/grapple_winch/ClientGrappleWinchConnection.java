package net.myriantics.klaxon.mechanics.grapple_winch;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.networking.c2s.GrappleWinchCableLengthUpdateC2S;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public class ClientGrappleWinchConnection extends GrappleWinchConnection {
    private Vec3d playerFallbackPos;
    private Vec3d playerFallbackPrevPos;
    private Vec3d hookFallbackPos;
    private Vec3d hookFallbackPrevPos;

    private final int playerId;
    private final int hookId;
    private final ClientGrappleWinchConnectionManager manager;

    private int ticksSinceUpdated;
    private AbstractClientPlayerEntity player = null;
    private GrapplingHook hook = null;

    public ClientGrappleWinchConnection(ClientGrappleWinchConnectionManager manager, int connectionId, int playerId, int hookId) {
        super(connectionId);
        this.playerId = playerId;
        this.hookId = hookId;
        this.manager = manager;
        this.updateEntities();
    }

    public ClientGrappleWinchConnection(ClientGrappleWinchConnectionManager manager, GrappleWinchConnectionSyncPacket packet) {
        this(manager, packet.connectionId(), packet.playerId(), packet.hookId());
        this.playerFallbackPos = packet.playerFallbackPos();
        this.playerFallbackPrevPos = packet.playerFallbackPos();
        this.hookFallbackPos = packet.hookFallbackPos();
        this.hookFallbackPrevPos = packet.hookFallbackPos();
        this.hookAnchored = packet.hookAnchored();
        this.updateEntities();
    }

    public void sync(GrappleWinchConnectionSyncPacket packet) {
        // update fallback positions
        this.playerFallbackPrevPos = this.playerFallbackPos;
        this.playerFallbackPos = packet.playerFallbackPos();
        this.hookFallbackPrevPos = this.hookFallbackPos;
        this.hookFallbackPos = packet.hookFallbackPos();

        this.hookAnchored = packet.hookAnchored();

        // update cable length
        if (this.player != null && !this.player.equals(MinecraftClient.getInstance().player)) {
            this.setCableLength(packet.cableLength());
        }
        this.maxCableLength = packet.maxCableLength();

        this.ticksSinceUpdated = 0;
        this.updateEntities();
    }

    @Override
    public void tick() {
        this.ticksSinceUpdated++;

        if (this.ticksSinceUpdated > manager.ticksSinceUpdated()) {
            this.manager.disconnect(this.getId(), CableDetachmentReason.GENERIC_DISCONNECT);
        }

        this.retracting = player.isUsingItem() && player.getActiveItem().isOf(KlaxonItems.GRAPPLE_WINCH);

        this.updateEntities();

        super.tick();

        // only do movement stuff if we're the client main player
        if (this.player.equals(MinecraftClient.getInstance().player)) {
            Vec3d compiledPlayerVec = Vec3d.ZERO;

            // initialize values
            Vec3d playerToHookVec = this.getHookPos().subtract(this.getPlayerPos().add(0, this.player.getEyeHeight(this.player.getPose()), 0));
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
                        ? this.maxCableLength
                        : Math.min(this.maxCableLength, cableLength)
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

    private void updateEntities() {
        this.player = (AbstractClientPlayerEntity) this.manager.getWorld().getEntityById(this.playerId);
        this.hook = (GrapplingHook) this.manager.getWorld().getEntityById(this.hookId);
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
        return this.hook == null || this.hook.klaxon$asEntity().isRemoved() ? this.hookFallbackPos : this.hook.klaxon$asEntity().getPos();
    }

    @Override
    public Vec3d getPlayerPos() {
        return this.player == null || this.player.isRemoved() ? this.playerFallbackPos : this.player.getPos();
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
    public void setCableLength(double cableLength) {
        super.setCableLength(cableLength);
        ClientPlayNetworking.send(new GrappleWinchCableLengthUpdateC2S(this.cableLength));
    }

    @Override
    public void resetCableLength() {
        this.setCableLength(this.getPlayerPos().distanceTo(this.getHookPos()));
    }
}
