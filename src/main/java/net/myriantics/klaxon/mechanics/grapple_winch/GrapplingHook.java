package net.myriantics.klaxon.mechanics.grapple_winch;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.ItemUsageLockoutTrigger;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import org.jetbrains.annotations.Nullable;

public interface GrapplingHook {
    void klaxon$onConnect(ServerPlayerEntity serverPlayer);

    void klaxon$onDisconnect(CableDetachmentReason reason);

    boolean klaxon$isAnchored();

    void klaxon$deAnchor(Vec3d deAnchoringDirection);

    Entity klaxon$asEntity();

    ItemStack klaxon$getItemStack();

    /**
     * Attempt to perform a fast-reloading operation. Plays a sound, emits game event, discards self, and detaches grapple cable if successful.
     * @param player - Player that is attempting to fast-reload this Grappling Hook into their Grapple Winch
     * @param winchStack - Stack that we're attempting to load into
     * @return Whether the fast loading succeeded or not
     */
    default boolean klaxon$tryFastReload(PlayerEntity player, ItemStack winchStack) {
        World world = player.getWorld();

        // check if the winch stack is a grapple winch
        if (!(winchStack.getItem() instanceof GrappleWinchItem)) {
            return false;
        }

        @Nullable GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) world).klaxon$get();
        @Nullable GrappleWinchConnection connection = manager == null ? null : manager.fromHook(this);

        ChargedProjectilesComponent projectiles = winchStack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);

        // make sure that the grapple winch is empty and that we're either unattached or being loaded into the attached player's grapple winch
        if (projectiles.isEmpty() && (connection == null || player.equals(connection.getPlayer()))) {
            // this is needed so players can choose whether they want to recast grapple claw or not
            // only trigger this if pickup occurred while retracting
            if (player instanceof ServerPlayerEntity serverPlayer && (connection == null || connection.isRetracting())) {
                // update usage lockout if true
                KlaxonServerPlayNetworkHandler.send(serverPlayer, new ItemUsageLockoutTrigger());
            }

            // if we're on the server, update the grapple winch's components to include this one
            if (!world.isClient()) {
                winchStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(this.klaxon$getItemStack()));
            }

            // play sounds and emit game event
            world.playSound(
                    null,
                    player.getX(),
                    player.getEyeY(),
                    player.getZ(),
                    KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_FAST_LOAD,
                    SoundCategory.PLAYERS,
                    0.7f + world.getRandom().nextFloat() * 0.3f,
                    0.7f + world.getRandom().nextFloat() * 0.3f
            );
            world.emitGameEvent(
                    GameEvent.ENTITY_ACTION,
                    player.getEyePos(),
                    GameEvent.Emitter.of(player)
            );

            if (!this.klaxon$asEntity().getWorld().isClient()) {
                assert manager != null;
                this.klaxon$asEntity().discard();
                if (connection != null) {
                    manager.disconnect(connection.getId(), CableDetachmentReason.FAST_RELOADED);
                }
            }

            return true;
        }

        return false;
    }

    @Nullable Entity klaxon$getHookedEntity();
}
