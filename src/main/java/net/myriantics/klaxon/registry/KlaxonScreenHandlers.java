package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.networking.packets.BlastProcessorScreenSyncPacket;

public class KlaxonScreenHandlers {
    public static final ExtendedScreenHandlerType<DeepslateBlastProcessorScreenHandler> BLAST_PROCESSOR_SCREEN_HANDLER
            = new ExtendedScreenHandlerType<>(DeepslateBlastProcessorScreenHandler::new);

    private static void createScreenHandler(String name, ScreenHandlerType<?> type) {
        Registry.register(Registries.SCREEN_HANDLER, Identifier.tryParse(name), type);
    }

    public static void registerModScreenHandlers() {
        KlaxonCommon.LOGGER.info("Registering KLAXON's ScreenHandlers!");

        createScreenHandler(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.asItem().toString(), BLAST_PROCESSOR_SCREEN_HANDLER);
    }
}
