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

import java.util.LinkedList;

public abstract class GrappleWinchConnectionManager extends PersistentState {
    protected final World world;
    protected final LinkedList<Runnable> disconnectQueue = new LinkedList<>();

    protected GrappleWinchConnectionManager(World world) {
        this.world = world;
    }

    public void tick() {
        while (!this.disconnectQueue.isEmpty()) {
            this.disconnectQueue.removeFirst().run();
        }
    }

    public World getWorld() {
        return this.world;
    }

    public abstract @Nullable GrappleWinchConnection fromPlayer(PlayerEntity player);

    public abstract @Nullable GrappleWinchConnection fromConnectionId(int connectionId);

    public abstract @Nullable GrappleWinchConnection fromHook(GrapplingHook hook);

    public final void disconnect(int connectionId, CableDetachmentReason reason) {
        this.disconnectQueue.add(() -> this.disconnectInternal(connectionId, reason));
    }

    protected abstract void disconnectInternal(int connectionId, CableDetachmentReason reason);

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        throw new AssertionError();
    }

    public interface Access {
        default GrappleWinchConnectionManager klaxon$getGrappleWinchConnectionManager() {
            return null;
        }
    }

    public static GrappleWinchConnectionManager get(World world) {
        @Nullable GrappleWinchConnectionManager manager = ((Access) world).klaxon$getGrappleWinchConnectionManager();
        if (manager == null) {
            throw new AssertionError("Grapple Winch Connection Manager not present in " + world + '.');
        } else {
            return manager;
        }
    }
}
