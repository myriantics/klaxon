package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorMenu;
import net.myriantics.klaxon.block.machines.precision_dispenser.PrecisionDispenserMenu;
import net.myriantics.klaxon.networking.s2c.BlastProcessorMenuPowerSyncPacket;

public abstract class KlaxonMenuTypes {
    public static final Holder<ExtendedScreenHandlerType<DeepslateBlastProcessorMenu, BlastProcessorMenuPowerSyncPacket>> DEEPSLATE_BLAST_PROCESSOR = register(
            "deepslate_blast_processor",
            new ExtendedScreenHandlerType<>(DeepslateBlastProcessorMenu::new, BlastProcessorMenuPowerSyncPacket.PACKET_CODEC)
    );
    public static final Holder<MenuType<PrecisionDispenserMenu>> PRECISION_DISPENSER = registerSimple(
            "precision_dispenser",
            PrecisionDispenserMenu::new
    );

    @SuppressWarnings("unchecked")
    private static <T extends AbstractContainerMenu, H extends MenuType<T>> Holder<H> registerSimple(String name, MenuType.MenuSupplier<T> supplier) {
        return (Holder<H>) register(name, new MenuType<>(supplier, FeatureFlags.VANILLA_SET));
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractContainerMenu, H extends MenuType<T>> Holder<H> register(String name, H type) {
        return (Holder<H>) Registry.registerForHolder(BuiltInRegistries.MENU, KlaxonCommon.locate(name), type);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's ScreenHandlers!");
    }
}
