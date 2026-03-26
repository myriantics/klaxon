package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.networking.s2c.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonScreenHandlers {
    public static final Holder<ExtendedScreenHandlerType<DeepslateBlastProcessorScreenHandler, BlastProcessorScreenSyncPacket>> BLAST_PROCESSOR_SCREEN_HANDLER = createScreenHandler(
            "deepslate_blast_processor",
            new ExtendedScreenHandlerType<>(DeepslateBlastProcessorScreenHandler::new, BlastProcessorScreenSyncPacket.PACKET_CODEC)
    );

    @SuppressWarnings("unchecked")
    private static <T extends AbstractContainerMenu, H extends MenuType<T>> Holder<H> createScreenHandler(String name, H type) {
        return (Holder<H>) Registry.registerForHolder(BuiltInRegistries.MENU, KlaxonCommon.locate(name), type);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's ScreenHandlers!");
    }
}
