package net.myriantics.klaxon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.myriantics.klaxon.client.particle.HallnoxDripParticle;
import net.myriantics.klaxon.client.particle.NetherReactionExplosionEmitterParticle;
import net.myriantics.klaxon.client.particle.NetherReactionExplosionLargeParticle;
import net.myriantics.klaxon.entity.GrappleClawEntityRenderer;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonEventListeners;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorScreen;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import net.myriantics.klaxon.registry.misc.KlaxonParticleTypes;
import net.myriantics.klaxon.registry.misc.KlaxonScreenHandlers;
import net.myriantics.klaxon.registry.render.KlaxonItemModelPredicates;

public class KlaxonClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // block transparency
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.HALLNOX_BULB, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.HALLNOX_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.HALLNOX_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.STEEL_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.STEEL_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.CRUDE_STEEL_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.NETHER_REACTOR_CORE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE, RenderLayer.getCutout());

        // block entity renderers
        BlockEntityRendererFactories.register(KlaxonBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY, SignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(KlaxonBlockEntities.CUSTOM_HANGING_SIGN_BLOCK_ENTITY, HangingSignBlockEntityRenderer::new);

        EntityRendererRegistry.register(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, GrappleClawEntityRenderer::new);

        // handled screens
        HandledScreens.register(KlaxonScreenHandlers.BLAST_PROCESSOR_SCREEN_HANDLER, DeepslateBlastProcessorScreen::new);

        // packets
        KlaxonPackets.registerS2CPacketRecievers();

        // client event listeners
        KlaxonEventListeners.initClient();

        // model predicates
        KlaxonItemModelPredicates.init();

        // particles
        ParticleFactoryRegistry.getInstance().register(KlaxonParticleTypes.HALLNOX_POD_DRIP, HallnoxDripParticle.HallnoxDripParticleFactory::new);
        ParticleFactoryRegistry.getInstance().register(KlaxonParticleTypes.NETHER_REACTION_EXPLOSION, NetherReactionExplosionLargeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(KlaxonParticleTypes.NETHER_REACTION_EXPLOSION_EMITTER, new NetherReactionExplosionEmitterParticle.Factory());
    }
}
