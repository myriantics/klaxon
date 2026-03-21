package net.myriantics.klaxon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.resource.loader.FabricLifecycledResourceManager;
import net.fabricmc.fabric.impl.resource.loader.FabricResource;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.PackType;
import net.myriantics.klaxon.particle.HallnoxDripParticle;
import net.myriantics.klaxon.particle.NetherReactionExplosionEmitterParticle;
import net.myriantics.klaxon.particle.NetherReactionExplosionLargeParticle;
import net.myriantics.klaxon.entity.GrappleClawEntityRenderer;
import net.myriantics.klaxon.registry.*;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.resource.KlaxonSplashTextResourceSupplier;
import net.myriantics.klaxon.screen.DeepslateBlastProcessorScreen;
import net.myriantics.klaxon.registry.misc.KlaxonParticleTypes;
import net.myriantics.klaxon.registry.misc.KlaxonScreenHandlers;

public class KlaxonClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // block transparency
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.HALLNOX_BULB, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.HALLNOX_DOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.HALLNOX_TRAPDOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.STEEL_DOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.STEEL_TRAPDOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.CRUDE_STEEL_DOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.NETHER_REACTOR_CORE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE, RenderType.cutout());

        EntityRendererRegistry.register(KlaxonEntityTypes.GRAPPLE_CLAW, GrappleClawEntityRenderer::new);

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
