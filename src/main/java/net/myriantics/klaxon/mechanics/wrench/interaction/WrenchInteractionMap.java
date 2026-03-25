package net.myriantics.klaxon.mechanics.wrench.interaction;

import net.minecraft.world.phys.Vec2;
import net.myriantics.klaxon.mechanics.wrench.interaction.layers.QuadInteractionMapLayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class WrenchInteractionMap {

    private final List<InteractionMapLayer> layers = new ArrayList<>();

    private WrenchInteractionMap() {
    }

    public static WrenchInteractionMap create() {
        return new WrenchInteractionMap();
    }

    public WrenchInteractionMap add(InteractionMapLayer layer) {
        this.layers.add(layer);
        return this;
    }

    public static WrenchInteractionMap fullBlock(WrenchInteraction actionType) {
        return create().add(QuadInteractionMapLayer.fullBlock(actionType));
    }

    public static WrenchInteractionMap split(WrenchInteraction left, WrenchInteraction right) {
        return create()
                .add(new QuadInteractionMapLayer(left, 0, 0, 8, 16))
                .add(new QuadInteractionMapLayer(right, 8, 0, 16, 16));
    }

    public WrenchInteraction select(Vec2 faceClickedPos) {
        for (int i = layers.size(); i > 0; i--) {
            InteractionMapLayer layer = layers.get(i - 1);
            @Nullable WrenchInteraction type = layer.getInteraction(faceClickedPos);
            if (type != null) {
                return type;
            }
        }
        return WrenchInteraction.NO_OP;
    }
}
