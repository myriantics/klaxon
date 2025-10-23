package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At(value = "TAIL")
    )
    private void klaxon$writePersistentData(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) this;

        @Nullable GrappleClawEntity grappleClaw = access.klaxon$getGrappleClaw();

        // write grapple claw data to player nbt like you would a vehicle
        if (grappleClaw != null) {
            NbtCompound grappleClawCompound = new NbtCompound();
            grappleClaw.saveNbt(grappleClawCompound);
            nbt.put(KlaxonNBTIds.ATTACHED_GRAPPLE_CLAW, grappleClawCompound);
            nbt.putDouble(KlaxonNBTIds.CURRENT_WINCH_CABLE_LENGTH, access.klaxon$getCurrentWinchCableLength());
        } else {
            nbt.remove(KlaxonNBTIds.ATTACHED_GRAPPLE_CLAW);
            nbt.remove(KlaxonNBTIds.CURRENT_WINCH_CABLE_LENGTH);
        }
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At(value = "TAIL")
    )
    private void klaxon$readPersistentData(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) this;

        if (nbt.contains(KlaxonNBTIds.ATTACHED_GRAPPLE_CLAW)) {
            NbtCompound grappleClawCompound = nbt.getCompound(KlaxonNBTIds.ATTACHED_GRAPPLE_CLAW);
            Optional<Entity> maybeGrappleClaw = EntityType.getEntityFromNbt(grappleClawCompound, getWorld());
            if (maybeGrappleClaw.isPresent() && maybeGrappleClaw.get() instanceof GrappleClawEntity grappleClaw) {
                access.klaxon$setGrappleClaw(grappleClaw);
                grappleClaw.attachCable((ServerPlayerEntity) (Object)this);
            }
        }

        if (nbt.containsUuid(KlaxonNBTIds.CURRENT_WINCH_CABLE_LENGTH)) {
            access.klaxon$setCurrentWinchCableLength(nbt.getDouble(KlaxonNBTIds.CURRENT_WINCH_CABLE_LENGTH));
        }
    }
}
