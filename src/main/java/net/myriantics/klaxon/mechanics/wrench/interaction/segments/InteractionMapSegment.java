package net.myriantics.klaxon.mechanics.wrench.interaction.segments;

import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.util.BlockFaceRegion;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InteractionMapSegment {

    private final BlockFaceRegion region;
    private final @Nullable WrenchInteraction interaction;

    private InteractionMapSegment(@Nullable WrenchInteraction interaction, BlockFaceRegion region) {
        this.interaction = interaction;
        this.region = region;
    }

    public static InteractionMapSegment of(@Nullable WrenchInteraction interaction, BlockFaceRegion region) {
        return new InteractionMapSegment(interaction, region);
    }

    public static InteractionMapSegment of(@Nullable WrenchInteraction interaction, float minX, float minY, float maxX, float maxY) {
        return of(interaction, BlockFaceRegion.of(minX, minY, maxX, maxY));
    }

    public static InteractionMapSegment fullBlock(@Nullable WrenchInteraction interaction) {
        return of(interaction, BlockFaceRegion.FULL_BLOCK);
    }

    public @Nullable WrenchInteraction getInteraction() {
        return this.interaction;
    }

    public BlockFaceRegion getRegion() {
        return this.region;
    }
}
