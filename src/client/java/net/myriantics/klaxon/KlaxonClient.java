package net.myriantics.klaxon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.myriantics.klaxon.registry.*;
import net.myriantics.klaxon.registry.item.KlaxonItemColors;
import net.myriantics.klaxon.registry.item.KlaxonItemModelPredicates;
import net.myriantics.klaxon.registry.network.KlaxonClientPackets;
import net.myriantics.klaxon.registry.render.*;
import net.myriantics.klaxon.resource.KlaxonSplashTextResourceSupplier;

public class KlaxonClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // block transparency
        KlaxonBlockRenderTypes.init();

        // entity renderers
        KlaxonEntityRenderers.init();

        // screens
        KlaxonScreens.init();

        // packets
        KlaxonClientPackets.registerS2CPacketRecievers();

        // client event listeners
        KlaxonClientEventListeners.initClient();

        // model predicates
        KlaxonItemModelPredicates.init();

        // entity model layers
        KlaxonEntityModelLayers.init();

        // particles
        KlaxonParticleFactories.init();

        // item tinting
        KlaxonItemColors.init();

        // splashes
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new KlaxonSplashTextResourceSupplier());
    }
}
