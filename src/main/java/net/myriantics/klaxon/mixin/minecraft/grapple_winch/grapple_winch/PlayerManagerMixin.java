package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_winch;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {


    @Inject(
            method = "remove",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;hasVehicle()Z")
    )
    private void klaxon$discardGrappleClawWhenLoggingOut(ServerPlayerEntity player, CallbackInfo ci) {
        if (((PlayerEntityGrappleAccess) player).klaxon$getGrappleClaw() instanceof GrappleClawEntity grappleClaw) {
            grappleClaw.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
    }

    @Inject(
            method = "onPlayerConnect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendStatusEffects(Lnet/minecraft/server/network/ServerPlayerEntity;)V")
    )
    private void klaxon$respawnGrappleClawWhenLoggingIn(
            ClientConnection connection,
            ServerPlayerEntity player,
            ConnectedClientData clientData,
            CallbackInfo ci,
            @Local Optional<NbtCompound> playerData
    ) {
        if (playerData.isPresent()) {
            ServerWorld serverWorld = player.getServerWorld();

            // yonk grapple claw from player nbt
            NbtCompound grappleClawNbtCompound = playerData.get().getCompound(KlaxonNBTIds.ATTACHED_GRAPPLE_CLAW);
            Entity entity = EntityType.loadEntityWithPassengers(
                    grappleClawNbtCompound,
                    serverWorld,
                    grappleClaw -> !serverWorld.tryLoadEntity(grappleClaw) ? null : grappleClaw
            );

            // try attaching grapple claw to player
            if (entity instanceof GrappleClawEntity grappleClaw) {
                serverWorld.spawnEntityAndPassengers(grappleClaw);
                grappleClaw.attachCable(player);
            }
        }
    }
}
