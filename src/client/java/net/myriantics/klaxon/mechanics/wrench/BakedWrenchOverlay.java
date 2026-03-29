package net.myriantics.klaxon.mechanics.wrench;

import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.util.BlockFaceRegion;

import java.util.ArrayList;
import java.util.List;

public class BakedWrenchOverlay {
    private final List<Entry> entries;

    private BakedWrenchOverlay(List<Entry> entries) {
        this.entries = entries;
    }

    public static BakedWrenchOverlay of(BlockFaceRegion base, WrenchActionContext.Manual context) {
        WrenchInteractionMap map = WrenchInteractionMap.create();
        for (int i = 0; i < map.segments.size(); i++) {
            // InteractionMapSegment layer = map.segments.get(i);
            ArrayList<BlockFaceRegion> list = new ArrayList<>();
            for (int j = i; i < map.segments.size(); i++) {

            }
        }

        return new BakedWrenchOverlay(List.of());
    }

    private static class Entry {
        private final List<BlockFaceRegion> regions;
        private final WrenchActionType type;
        private final boolean selected;

        private Entry(List<BlockFaceRegion> bounds, WrenchActionType type, boolean selected) {
            this.regions = bounds;
            this.type = type;
            this.selected = selected;
        }

        private static class Builder {

        }
    }
}
