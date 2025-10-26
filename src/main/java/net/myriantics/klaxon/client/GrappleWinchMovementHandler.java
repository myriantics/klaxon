package net.myriantics.klaxon.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchItem;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.networking.KlaxonClientPlayNetworkHandler;
import net.myriantics.klaxon.networking.c2s.GrappleWinchCableForceDisconnectC2S;

public final class GrappleWinchMovementHandler implements ClientPlayerTickable {
    private final ClientPlayerEntity clientPlayer;

    public GrappleWinchMovementHandler(ClientPlayerEntity clientPlayer) {
        this.clientPlayer = clientPlayer;
    }

    @Override
    public void tick() {
        // check if we're connected before doing anything
        if (access().klaxon$hasActiveConnection()) {

            // if we can't support the grapple winch cable, disconnect the grapple claw
            if (!canSupportGrappleWinchCable()) {
                detachGrappleCable(GrappleClawEntity.CableDetachmentReason.INVALID_HELD_ITEMS);
            }

            // if
        }
    }

    private void detachGrappleCable(GrappleClawEntity.CableDetachmentReason reason) {
        GrappleClawEntity claw = access().klaxon$getGrappleClaw();
        if (claw == null) {
            return;
        }

        access().klaxon$setGrappleClaw(null);
        access().klaxon$setConnectionData(null);
        GrappleWinchClientConnectionManager.INSTANCE.discardConnection(this.clientPlayer.getId());
        claw.cableAttachmentHandler.detach(reason);
        KlaxonClientPlayNetworkHandler.send(new GrappleWinchCableForceDisconnectC2S(reason));
    }

    private PlayerEntityGrappleAccess access() {
        return (PlayerEntityGrappleAccess) this.clientPlayer;
    }

    private boolean canSupportGrappleWinchCable() {
        return stackSupportsGrappleWinchCable(this.clientPlayer.getMainHandStack()) || stackSupportsGrappleWinchCable(this.clientPlayer.getOffHandStack());
    }

    private boolean stackSupportsGrappleWinchCable(ItemStack stack) {
        return stack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(stack);
    }
}
