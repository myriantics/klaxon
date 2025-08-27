package net.myriantics.klaxon.registry.render;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

public abstract class KlaxonVertexFormats {
    public static final VertexFormat POSITION_COLOR_LIGHT_NORMAL = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .add("UV2", VertexFormatElement.UV_2)
            .add("Normal", VertexFormatElement.NORMAL)
            .skip(1)
            .build();
}
