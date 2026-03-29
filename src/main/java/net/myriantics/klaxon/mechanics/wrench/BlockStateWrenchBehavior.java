package net.myriantics.klaxon.mechanics.wrench;

import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;

public abstract class BlockStateWrenchBehavior<T extends Comparable<T>> {
    private final Property<T> property;
    private final TagKey<Block> allowlistTag;
    private final TagKey<Block> denylistTag;

    private static final String ALLOWLIST_SUBDIRECTORY = "wrench_behavior/allowlist";
    private static final String DENYLIST_SUBDIRECTORY = "wrench_behavior/denylist";

    private static final WrenchInteractionMap EMPTY = WrenchInteractionMap.create();

    public BlockStateWrenchBehavior(
            Property<T> property,
            ResourceLocation id
    ) {
        this.property = property;
        this.allowlistTag = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), ALLOWLIST_SUBDIRECTORY + "_" + id.getPath()));
        this.denylistTag = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), DENYLIST_SUBDIRECTORY + "_" + id.getPath()));
    }

    public Property<T> getProperty() {
        return property;
    }

    public TagKey<Block> getAllowlistTag() {
        return allowlistTag;
    }

    public TagKey<Block> getDenylistTag() {
        return denylistTag;
    }

    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        return EMPTY;
    }

    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        return WrenchInteraction.NO_OP;
    }

    public boolean test(BlockState state) {
        return !state.is(this.getDenylistTag()) && state.is(this.getAllowlistTag()) && state.hasProperty(property);
    }

    protected abstract Optional<T> applyManual(T original, ManualWrenchInteractionContext context);

    protected abstract Optional<T> applyDispenser(T original, DispenserWrenchInteractionContext context);
}
