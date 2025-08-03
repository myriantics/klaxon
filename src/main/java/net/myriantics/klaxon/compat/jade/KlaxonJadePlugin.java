package net.myriantics.klaxon.compat.jade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.functional.HallnoxPodBlock;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.compat.jade.providers.HallnoxPodStatusProvider;
import net.myriantics.klaxon.compat.jade.providers.DeepslateBlastProcessorProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.addon.harvest.SimpleToolHandler;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;

import java.util.ArrayList;
import java.util.List;

public class KlaxonJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(DeepslateBlastProcessorProvider.INSTANCE, DeepslateBlastProcessorBlock.class);
        IWailaPlugin.super.register(registration);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(
                KlaxonCommon.locate("hammer"),
                List.of(
                        KlaxonItems.STEEL_HAMMER
                )
        ));
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(
                KlaxonCommon.locate("wrench"),
                List.of(
                        KlaxonItems.STEEL_WRENCH
                )
        ));

        registration.registerBlockComponent(DeepslateBlastProcessorProvider.INSTANCE, DeepslateBlastProcessorBlock.class);
        registration.registerBlockComponent(HallnoxPodStatusProvider.INSTANCE, HallnoxPodBlock.class);

        IWailaPlugin.super.registerClient(registration);
    }

    // called in ShearsToolHandlerMixin
    public static List<ItemStack> appendCableShears(List<ItemStack> original) {
        List<ItemStack> appendedList = new ArrayList<>(original);
        appendedList.add(KlaxonItems.STEEL_CABLE_SHEARS.getDefaultStack());
        return List.copyOf(appendedList);
    }

    // called in HarvestToolProviderMixin
    public static List<Item> appendCleaver(List<Item> original) {
        List<Item> appendedList = new ArrayList<>(original);
        appendedList.add(KlaxonItems.STEEL_CLEAVER);
        return List.copyOf(appendedList);
    }
}
