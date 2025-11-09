package net.myriantics.klaxon.mechanics.grapple_winch.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import org.jetbrains.annotations.Nullable;

public abstract sealed class GrappleWinchConnectionManager extends PersistentState permits ClientGrappleWinchConnectionManager, ServerGrappleWinchConnectionManager {
    protected final World world;

    protected GrappleWinchConnectionManager(World world) {
        this.world = world;
    }

    public abstract void tick();

    public World getWorld() {
        return this.world;
    }

    public abstract @Nullable GrappleWinchConnection fromPlayer(PlayerEntity player);

    public abstract @Nullable GrappleWinchConnection fromConnectionId(int connectionId);

    public abstract @Nullable GrappleWinchConnection fromHook(GrapplingHook hook);

    public void disconnect(int connectionId, CableDetachmentReason reason) {
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        throw new AssertionError();
    }

    public interface Access {
        default GrappleWinchConnectionManager klaxon$get() {
            return null;
        }
    }
}
