package net.myriantics.klaxon.compat.jade;

import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.addon.harvest.SimpleToolHandler;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;

import java.util.List;

public class KlaxonJadePlugin implements IWailaPlugin {
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
        IWailaPlugin.super.registerClient(registration);
    }
}
