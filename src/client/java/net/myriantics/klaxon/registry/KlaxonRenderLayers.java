package net.myriantics.klaxon.registry;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;
import net.myriantics.klaxon.mixin.minecraft.rendering.RenderTypeInvoker;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalDouble;

public abstract class KlaxonRenderLayers {
    private static final CompositeRenderType GRAPPLE_WINCH_CABLE = of(
            "grapple_winch_cable",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINE_STRIP,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(8)))
                    .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false)
    );

    public static RenderType.CompositeRenderType getGrappleWinchCable() {
        return GRAPPLE_WINCH_CABLE;
    }

    public static CompositeRenderType of(
            String name, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int expectedBufferSize, RenderType.CompositeState phaseData
    ) {
        return of(name, vertexFormat, drawMode, expectedBufferSize, false, false, phaseData);
    }

    public static CompositeRenderType of(
            String name,
            VertexFormat vertexFormat,
            VertexFormat.Mode drawMode,
            int expectedBufferSize,
            boolean hasCrumbling,
            boolean translucent,
            RenderType.CompositeState phases
    ) {
        return RenderTypeInvoker.klaxon$invokeCreateMultiphase(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
    }
}
