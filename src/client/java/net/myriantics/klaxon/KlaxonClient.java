package net.myriantics.klaxon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.packs.PackType;
import net.myriantics.klaxon.particle.HallnoxDripParticle;
import net.myriantics.klaxon.particle.NetherReactionExplosionEmitterParticle;
import net.myriantics.klaxon.particle.NetherReactionExplosionLargeParticle;
import net.myriantics.klaxon.entity.GrappleClawEntityRenderer;
import net.myriantics.klaxon.registry.*;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonItemColors;
import net.myriantics.klaxon.registry.item.KlaxonItemModelPredicates;
import net.myriantics.klaxon.registry.network.KlaxonClientPackets;
import net.myriantics.klaxon.registry.render.KlaxonBlockRenderTypes;
import net.myriantics.klaxon.registry.render.KlaxonEntityModelLayers;
import net.myriantics.klaxon.registry.render.KlaxonEntityRenderers;
import net.myriantics.klaxon.resource.KlaxonSplashTextResourceSupplier;
import net.myriantics.klaxon.screen.DeepslateBlastProcessorScreen;
import net.myriantics.klaxon.registry.misc.KlaxonParticleTypes;
import net.myriantics.klaxon.registry.misc.KlaxonScreenHandlers;

public class KlaxonClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // block transparency
        KlaxonBlockRenderTypes.init();

        // entity renderers
        KlaxonEntityRenderers.init();

        // handled screens
        MenuScreens.register(KlaxonScreenHandlers.BLAST_PROCESSOR_SCREEN_HANDLER, DeepslateBlastProcessorScreen::new);

        // packets
        KlaxonClientPackets.registerS2CPacketRecievers();

        // client event listeners
        KlaxonClientEventListeners.initClient();

        // model predicates
        KlaxonItemModelPredicates.init();

        // entity model layers
        KlaxonEntityModelLayers.init();

        // particles
        ParticleFactoryRegistry.getInstance().register(KlaxonParticleTypes.HALLNOX_POD_DRIP, HallnoxDripParticle.HallnoxDripParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(KlaxonParticleTypes.NETHER_REACTION_EXPLOSION, NetherReactionExplosionLargeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(KlaxonParticleTypes.NETHER_REACTION_EXPLOSION_EMITTER, new NetherReactionExplosionEmitterParticle.Factory());

        // item tinting
        KlaxonItemColors.init();

        // splashes
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new KlaxonSplashTextResourceSupplier());
    }
}
