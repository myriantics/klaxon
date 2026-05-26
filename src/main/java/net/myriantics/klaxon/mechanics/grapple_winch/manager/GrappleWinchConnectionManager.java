package net.myriantics.klaxon.mechanics.grapple_winch.manager;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public abstract class GrappleWinchConnectionManager extends SavedData {
    protected final Level world;
    protected final LinkedList<Runnable> disconnectQueue = new LinkedList<>();

    protected GrappleWinchConnectionManager(Level world) {
        this.world = world;
    }

    public void tick() {
        while (!this.disconnectQueue.isEmpty()) {
            this.disconnectQueue.removeFirst().run();
        }
    }

    public Level getLevel() {
        return this.world;
    }

    public abstract @Nullable GrappleWinchConnection fromPlayer(Player player);

    public abstract @Nullable GrappleWinchConnection fromConnectionId(int connectionId);

    public abstract @Nullable GrappleWinchConnection fromHook(GrapplingHook hook);

    public final void disconnect(int connectionId, CableDetachmentReason reason) {
        this.disconnectQueue.add(() -> this.disconnectInternal(connectionId, reason));
    }

    protected abstract void disconnectInternal(int connectionId, CableDetachmentReason reason);

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        throw new AssertionError();
    }

    public interface Access {
        default GrappleWinchConnectionManager klaxon$getGrappleWinchConnectionManager() {
            return null;
        }
    }

    public static GrappleWinchConnectionManager get(Level world) {
        @Nullable GrappleWinchConnectionManager manager = ((Access) world).klaxon$getGrappleWinchConnectionManager();
        if (manager == null) {
            throw new AssertionError("Grapple Winch Connection Manager not present in " + world + '.');
        } else {
            return manager;
        }
    }
}
