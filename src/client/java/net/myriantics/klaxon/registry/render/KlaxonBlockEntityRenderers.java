package net.myriantics.klaxon.registry.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.render.blockentityrenderers.CreativeContactChargerBlockEntityRenderer;

public abstract class KlaxonBlockEntityRenderers {

    static {
        register(KlaxonBlockEntityTypes.CREATIVE_CONTACT_CHARGER.value(), CreativeContactChargerBlockEntityRenderer::new);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Block Entity Renderers!");
    }

    private static <T extends BlockEntity> void register(Holder<BlockEntityType<T>> holder, BlockEntityRendererProvider<T> provider) {
        register(holder.value(), provider);
    }

    private static <T extends BlockEntity> void register(BlockEntityType<T> type, BlockEntityRendererProvider<T> provider) {
        BlockEntityRenderers.register(type, provider);
    }
}
