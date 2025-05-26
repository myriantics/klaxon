package net.myriantics.klaxon.registry.minecraft;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.networking.s2c.BlastProcessorScreenSyncPacket;

public class KlaxonScreenHandlers {
    public static final ExtendedScreenHandlerType<DeepslateBlastProcessorScreenHandler, BlastProcessorScreenSyncPacket> BLAST_PROCESSOR_SCREEN_HANDLER
            = new ExtendedScreenHandlerType<>(DeepslateBlastProcessorScreenHandler::new, BlastProcessorScreenSyncPacket.PACKET_CODEC);

    private static void createScreenHandler(String name, ScreenHandlerType type) {
        Registry.register(Registries.SCREEN_HANDLER, Identifier.tryParse(name), type);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's ScreenHandlers!");

        createScreenHandler(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.asItem().toString(), BLAST_PROCESSOR_SCREEN_HANDLER);
    }
}
