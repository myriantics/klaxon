package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.networking.s2c.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

public abstract class KlaxonScreenHandlers {
    public static final ExtendedScreenHandlerType<DeepslateBlastProcessorScreenHandler, BlastProcessorScreenSyncPacket> BLAST_PROCESSOR_SCREEN_HANDLER
            = new ExtendedScreenHandlerType<>(DeepslateBlastProcessorScreenHandler::new, BlastProcessorScreenSyncPacket.PACKET_CODEC);

    private static void createScreenHandler(String name, MenuType type) {
        Registry.register(BuiltInRegistries.MENU, ResourceLocation.tryParse(name), type);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's ScreenHandlers!");

        createScreenHandler(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.asItem().toString(), BLAST_PROCESSOR_SCREEN_HANDLER);
    }
}
