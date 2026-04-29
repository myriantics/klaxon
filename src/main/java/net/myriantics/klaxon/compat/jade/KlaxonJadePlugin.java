package net.myriantics.klaxon.compat.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.functional.hallnox_pod.HallnoxPodBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.steel.SteelBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.precision_dispenser.PrecisionDispenserBlock;
import net.myriantics.klaxon.compat.jade.providers.block.DeepslateBlastProcessorProvider;
import net.myriantics.klaxon.compat.jade.providers.block.MufflableBlockProvider;
import net.myriantics.klaxon.compat.jade.providers.entity.GrappleClawEntityProvider;
import net.myriantics.klaxon.compat.jade.providers.block.HallnoxPodStatusProvider;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.mechanics.muffling.MufflableBlock;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.addon.harvest.SimpleToolHandler;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;

import java.util.ArrayList;
import java.util.List;

public class KlaxonJadePlugin implements IWailaPlugin {

    private static final ArrayList<Class<? extends Block>> MUFFLABLE_BLOCKS = new ArrayList<>();

    static {
        registerMufflable(SteelBlastProcessorBlock.class);
        registerMufflable(PrecisionDispenserBlock.class);
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
        for (Class<? extends Block> clazz : MUFFLABLE_BLOCKS) {
            registration.registerBlockDataProvider(MufflableBlockProvider.INSTANCE, clazz);
        }

        registration.registerBlockDataProvider(DeepslateBlastProcessorProvider.INSTANCE, DeepslateBlastProcessorBlock.class);
        registration.registerEntityDataProvider(GrappleClawEntityProvider.INSTANCE, GrappleClawEntity.class);
        IWailaPlugin.super.register(registration);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(
                KlaxonCommon.locate("hammer"),
                List.of(
                        KlaxonItems.STEEL_HAMMER.value()
                )
        ));
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(
                KlaxonCommon.locate("wrench"),
                List.of(
                        KlaxonItems.STEEL_WRENCH.value()
                )
        ));

        // can't be an interface apparently
        for (Class<? extends Block> clazz : MUFFLABLE_BLOCKS) {
            registration.registerBlockComponent(MufflableBlockProvider.INSTANCE, clazz);
        }

        registration.registerBlockComponent(DeepslateBlastProcessorProvider.INSTANCE, DeepslateBlastProcessorBlock.class);
        registration.registerBlockComponent(HallnoxPodStatusProvider.INSTANCE, HallnoxPodBlock.class);
        registration.registerEntityComponent(GrappleClawEntityProvider.INSTANCE, GrappleClawEntity.class);
        registration.registerEntityIcon(GrappleClawEntityProvider.INSTANCE, GrappleClawEntity.class);

        IWailaPlugin.super.registerClient(registration);
    }

    // called in ShearsToolHandlerMixin
    public static List<ItemStack> appendCableShears(List<ItemStack> original) {
        List<ItemStack> appendedList = new ArrayList<>(original);
        appendedList.add(KlaxonItems.STEEL_CABLE_SHEARS.value().getDefaultInstance());
        return List.copyOf(appendedList);
    }

    // called in HarvestToolProviderMixin
    public static List<Item> appendCleaver(List<Item> original) {
        List<Item> appendedList = new ArrayList<>(original);
        appendedList.add(KlaxonItems.STEEL_CLEAVER.value());
        return List.copyOf(appendedList);
    }

    public static ResourceLocation locate(String name) {
        return KlaxonCommon.locate(name);
    }

    public static String textTranslationKey(ResourceLocation location, String suffix) {
        return textTranslationKey(location) + "." + suffix;
    }

    public static String textTranslationKey(ResourceLocation location) {
        return location.getNamespace() + ".jade." + location.getPath();
    }

    public static String configTranslationKey(ResourceLocation location) {
        return "config.jade.plugin_" + location.getNamespace() + "." + location.getPath();
    }

    private static void registerMufflable(Class<? extends Block> mufflableBlockClass) {
        MUFFLABLE_BLOCKS.add(mufflableBlockClass);
    }
}
