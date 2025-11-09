package net.myriantics.klaxon.mechanics.grapple_winch;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface GrapplingHook {
    void klaxon$onConnect(ServerPlayerEntity serverPlayer);

    void klaxon$onDisconnect(CableDetachmentReason reason);

    boolean klaxon$isAnchored();

    boolean klaxon$deAnchor(Vec3d deAnchoringDirection);

    Entity klaxon$asEntity();

    /**
     * Attempt to perform a fast-reloading operation. Plays a sound, emits game event, discards self, and detaches grapple cable if successful.
     * @param player - Player that is attempting to fast-reload this Grappling Hook into their Grapple Winch
     * @param winchStack - Stack that we're attempting to load into
     * @return Whether the fast loading succeeded or not
     */
    boolean klaxon$tryFastReload(PlayerEntity player, ItemStack winchStack);

    @Nullable Entity klaxon$getHookedEntity();
}
