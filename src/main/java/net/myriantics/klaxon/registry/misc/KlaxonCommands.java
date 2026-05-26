package net.myriantics.klaxon.registry.misc;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManagerCommands;

public class KlaxonCommands {


    private static void handleCallback(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        GrappleWinchConnectionManagerCommands.register(dispatcher);
    }

    public static void init() {
        CommandRegistrationCallback.EVENT.register(KlaxonCommands::handleCallback);
        KlaxonCommon.LOGGER.info("Registered KLAXON's Command Registration Callback!");
    }
}
