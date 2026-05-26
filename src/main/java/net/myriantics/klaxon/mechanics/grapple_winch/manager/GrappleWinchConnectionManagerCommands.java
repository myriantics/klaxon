package net.myriantics.klaxon.mechanics.grapple_winch.manager;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Collections;

public abstract class GrappleWinchConnectionManagerCommands {

    private static final String DISCONNECT_SINGLE = "klaxon.commands.grapplewinchconnectionmanager.disconnect.single";
    private static final String DISCONNECT_MULTIPLE = "klaxon.commands.grapplewinchconnectionmanager.disconnect.multiple";
    private static final String CLEAR = "klaxon.commands.grapplewinchconnectionmanager.clear";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("grapplewinchconnectionmanager")
                        .then(
                                Commands.literal("disconnect")
                                        .executes(context -> disconnect(
                                                context.getSource(),
                                                Collections.singleton(context.getSource().getEntityOrException()),
                                                CableDetachmentReason.MANUAL_DISCONNECT
                                        ))
                                        .then(
                                                Commands.argument("targets", EntityArgument.entities())
                                                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                                                        .executes(context -> disconnect(
                                                                context.getSource(),
                                                                EntityArgument.getEntities(context, "targets"),
                                                                CableDetachmentReason.GENERIC_DISCONNECT
                                                        ))
                                        )
                        )
                        // anything past disconnecting own connection requires perms
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(
                                Commands.literal("clear")
                                        .executes(context -> clear(context.getSource()))
                        )

        );
    }

    private static int list(CommandSourceStack commandSourceStack) {
        ServerLevel serverLevel = commandSourceStack.getLevel();
        ServerGrappleWinchConnectionManager manager = ServerGrappleWinchConnectionManager.get(serverLevel);

        return 0;
    }

    private static int clear(CommandSourceStack commandSourceStack) {
        ServerLevel serverLevel = commandSourceStack.getLevel();
        ServerGrappleWinchConnectionManager manager = ServerGrappleWinchConnectionManager.get(serverLevel);

        if (manager.connectionId2Connection.isEmpty()) {
            return 0;
        }

        int disconnectionsPerformed = 0;

        for (int id : manager.connectionId2Connection.keySet()) {
            manager.disconnectInternal(id, CableDetachmentReason.GENERIC_DISCONNECT);
            disconnectionsPerformed++;
        }

        return disconnectionsPerformed;
    }

    private static <T extends Entity> int disconnect(
            @UnknownNullability CommandSourceStack commandSourceStackCommandContext,
            Collection<T> targets,
            CableDetachmentReason reason
    ) {

        if (targets.isEmpty()) {
            return 0; // no targets to affect
        }

        ServerLevel serverLevel = commandSourceStackCommandContext.getLevel();
        ServerGrappleWinchConnectionManager manager = ServerGrappleWinchConnectionManager.get(serverLevel);

        int disconnectionsPerformed = 0;

        for (Entity entity : targets) {
            ServerGrappleWinchConnection connection;
            if (entity instanceof ServerPlayer serverPlayer) {
                connection = manager.fromPlayer(serverPlayer);
            } else if (entity instanceof GrapplingHook grapplingHook) {
                connection = manager.fromHook(grapplingHook);
            } else {
                connection = null;
            }
            if (connection != null) {
                manager.disconnect(connection.getId(), reason);
                disconnectionsPerformed++;
            }
        }

        return disconnectionsPerformed;
    }
}
