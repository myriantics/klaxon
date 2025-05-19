package net.myriantics.klaxon.compat.jade;

import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.compat.jade.providers.DeepslateBlastProcessorProvider;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.addon.harvest.SimpleToolHandler;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;

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
                KlaxonCommon.locate("hammers"),
                List.of(
                        KlaxonItems.STEEL_HAMMER
                )
        ));
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(
                KlaxonCommon.locate("cleavers"),
                List.of(
                        KlaxonItems.STEEL_CLEAVER
                )
        ));
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(
                KlaxonCommon.locate("wrenches"),
                List.of(
                        KlaxonItems.STEEL_WRENCH
                )
        ));
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(
                KlaxonCommon.locate("cable_shears"),
                List.of(
                        KlaxonItems.STEEL_CABLE_SHEARS
                )
        ));

        registration.registerBlockComponent(DeepslateBlastProcessorProvider.INSTANCE, DeepslateBlastProcessorBlock.class);

        IWailaPlugin.super.registerClient(registration);
    }
}
