package net.myriantics.klaxon.mechanics.grapple_winch;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.networking.c2s.GrappleWinchCableLengthUpdateC2S;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import java.util.Objects;

public class ClientGrappleWinchConnection extends GrappleWinchConnection {
    private Vec3 playerFallbackPos;
    private Vec3 playerFallbackPrevPos;
    private Vec3 hookFallbackPos;
    private Vec3 hookFallbackPrevPos;

    private final int playerId;
    private final int hookId;
    private final ClientGrappleWinchConnectionManager manager;

    private int ticksSinceUpdated;
    private AbstractClientPlayer player = null;
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
        this.maxCableLength = packet.maxCableLength();
        this.setCableLength(packet.cableLength());
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
        if (this.player != null && (this.cableLength == -1 || !this.player.isLocalPlayer())) {
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

        if (this.player != null) {
            this.retracting = player.isUsingItem() && player.getUseItem().is(KlaxonItems.GRAPPLE_WINCH);
        }

        this.updateEntities();

        super.tick();

        // only do movement stuff if we're the client main player
        if (Objects.equals(this.player, Minecraft.getInstance().player)) {
            Vec3 compiledPlayerVec = Vec3.ZERO;

            // initialize values
            Vec3 playerToHookVec = this.getHookPos().subtract(this.getPlayerEyePos());
            double clawDistance = playerToHookVec.length();

            // update winch cable length
            if (this.retracting || (this.player.onGround() && clawDistance > this.cableLength)) {
                this.cableLength = clawDistance;
            }

            // make sure grapple claw is loaded and anchored
            if (isHookAnchored() && !EntityWeightHelper.isHeavy(this.player)) {

                // get movement vectors and normalize them
                playerToHookVec = playerToHookVec.normalize();
                Vec3 playerFacingVec = this.player.getViewVector(1.0f).normalize();

                // tick retraction movement
                if (this.retracting) {

                    // transform movement vectors
                    Vec3 playerToClawRetractionVec = playerToHookVec.scale(2./20);
                    // player can direct movement with facing direction to combat getting stuck under ledges
                    Vec3 playerFacingRetractionVec = playerFacingVec.scale(1./20).scale(this.player.isSprinting() ? 1.5 : 1);

                    // add vectors to self vector
                    if (!this.player.isShiftKeyDown()) {
                        compiledPlayerVec = compiledPlayerVec.add(playerToClawRetractionVec).add(playerFacingRetractionVec);
                    }
                }

                // apply velocity to player if they go past target range
                // retraction is only capped at the max range
                // cable length is also less regulated when sneaking & retracting so that players can descend with the grapple winch
                if (clawDistance > ((this.player.isShiftKeyDown() && this.retracting) || this.player.onGround()
                        ? this.maxCableLength
                        : Math.min(this.maxCableLength, cableLength)
                )) {
                    Vec3 playerRangeCorrectionVec = playerToHookVec.scale(0.1);
                    playerRangeCorrectionVec = playerRangeCorrectionVec.add(0, player.getGravity(), 0);
                    compiledPlayerVec = compiledPlayerVec.add(playerRangeCorrectionVec);
                }
            }

            // commit velocity
            player.push(compiledPlayerVec);
        }
    }

    public boolean validate() {
        return canPlayerSupportGrappleWinchCable();
    }

    private boolean canPlayerSupportGrappleWinchCable() {
        return stackSupportsGrappleWinchCable(this.player.getMainHandItem()) || stackSupportsGrappleWinchCable(this.player.getOffhandItem());
    }

    private boolean stackSupportsGrappleWinchCable(ItemStack stack) {
        return stack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(stack);
    }

    private void updateEntities() {
        this.player = (AbstractClientPlayer) this.manager.getLevel().getEntity(this.playerId);
        this.hook = (GrapplingHook) this.manager.getLevel().getEntity(this.hookId);
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
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public GrapplingHook getHook() {
        return this.hook;
    }

    @Override
    public Vec3 getHookPos() {
        return this.hook == null || this.hook.klaxon$asEntity().isRemoved() ? this.hookFallbackPos : this.hook.klaxon$asEntity().position();
    }

    @Override
    public Vec3 getPlayerEyePos() {
        return this.player == null || this.player.isRemoved() ? this.playerFallbackPos : this.player.position();
    }

    public Vec3 getLerpedPlayerPos(float delta) {
        if (this.player != null && !this.player.isRemoved()) {
            return this.player.getPosition(delta);
        }

        double lerpedX = Mth.lerp(delta, this.playerFallbackPrevPos.x(), this.playerFallbackPos.x());
        double lerpedY = Mth.lerp(delta, this.playerFallbackPrevPos.y(), this.playerFallbackPos.y());
        double lerpedZ = Mth.lerp(delta, this.playerFallbackPrevPos.z(), this.playerFallbackPos.z());
        return new Vec3(lerpedX, lerpedY, lerpedZ);
    }

    public Vec3 getLerpedHookPos(float delta) {
        if (this.hook != null && !this.hook.klaxon$asEntity().isRemoved()) {
            return this.hook.klaxon$asEntity().getPosition(delta);
        }

        double lerpedX = Mth.lerp(delta, this.hookFallbackPrevPos.x(), this.hookFallbackPos.x());
        double lerpedY = Mth.lerp(delta, this.hookFallbackPrevPos.y(), this.hookFallbackPos.y());
        double lerpedZ = Mth.lerp(delta, this.hookFallbackPrevPos.z(), this.hookFallbackPos.z());
        return new Vec3(lerpedX, lerpedY, lerpedZ);
    }

    @Override
    public void setCableLength(double cableLength) {
        super.setCableLength(cableLength);
        ClientPlayNetworking.send(new GrappleWinchCableLengthUpdateC2S(this.cableLength));
    }

    @Override
    public void resetCableLength() {
        this.setCableLength(this.getPlayerEyePos().distanceTo(this.getHookPos()));
    }
}
