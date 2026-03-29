package net.myriantics.klaxon.mechanics.wrench.interaction;

import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.segments.InteractionMapSegment;
import net.myriantics.klaxon.util.BlockFaceRegion;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class WrenchInteractionMap {

    public final List<InteractionMapSegment> segments = new ArrayList<>();

    private @Nullable BlockFaceRegion.State2Rotation state2Rotation = null;

    private WrenchInteractionMap() {
    }

    public static WrenchInteractionMap create() {
        return new WrenchInteractionMap();
    }

    public WrenchInteractionMap add(InteractionMapSegment layer) {
        this.segments.add(layer);
        return this;
    }

    public static WrenchInteractionMap fullBlock(WrenchInteraction interaction) {
        return create().add(InteractionMapSegment.fullBlock(interaction));
    }

    public static WrenchInteractionMap splitHorizontal(WrenchInteraction top, WrenchInteraction bottom) {
        return create()
                .add(InteractionMapSegment.of(bottom, 0, 0, 1f, 0.5f))
                .add(InteractionMapSegment.of(top, 0, 0.5f, 1, 1));
    }

    public static WrenchInteractionMap splitVertical(WrenchInteraction left, WrenchInteraction right) {
        return create()
                .add(InteractionMapSegment.of(left, 0, 0, 0.5f, 1))
                .add(InteractionMapSegment.of(right, 0.5f, 0, 1, 1));
    }

    public WrenchInteractionMap state2Rotation(BlockFaceRegion.State2Rotation state2Rotation) {
        this.state2Rotation = state2Rotation;
        return this;
    }

    public BlockFaceRegion.Rotation getRotation(BlockState state, WrenchActionContext.GuiOrientation orientation) {
        return this.state2Rotation == null ? BlockFaceRegion.Rotation.R0 : this.state2Rotation.getRotation(state, orientation);
    }

    public WrenchInteraction select(float x, float y) {
        for (int i = segments.size(); i > 0; i--) {
            InteractionMapSegment layer = segments.get(i - 1);
            if (layer.getRegion().contains(x, y)) {
                @Nullable WrenchInteraction interaction = layer.getInteraction();
                if (interaction != null) {
                    return interaction;
                }
            }
        }
        return WrenchInteraction.NO_OP;
    }
}
