package net.myriantics.klaxon.registry.render;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.networking.s2c.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.registry.misc.KlaxonScreenHandlers;
import net.myriantics.klaxon.screen.DeepslateBlastProcessorScreen;

public abstract class KlaxonScreens {

    static {
        register(KlaxonScreenHandlers.BLAST_PROCESSOR_SCREEN_HANDLER, DeepslateBlastProcessorScreen::new);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Screens!");
    }

    private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>, R extends MenuType<? extends M>> void register(Holder<R> typeHolder, MenuScreens.ScreenConstructor<M, U> factory) {
        register(typeHolder.value(), factory);
    }

    private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, U> factory) {
        MenuScreens.register(type, factory);
    }
}
