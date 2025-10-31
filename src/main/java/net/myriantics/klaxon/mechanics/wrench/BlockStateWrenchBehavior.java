package net.myriantics.klaxon.mechanics.wrench;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

import java.util.Optional;

public abstract class BlockStateWrenchBehavior<T extends Comparable<T>> {
    private final Property<T> property;
    private final TagKey<Block> allowlistTag;
    private final TagKey<Block> denylistTag;

    private static final String ALLOWLIST_SUBDIRECTORY = "wrench_behavior/allowlist";
    private static final String DENYLIST_SUBDIRECTORY = "wrench_behavior/denylist";

    public BlockStateWrenchBehavior(
            Property<T> property,
            Identifier id
    ) {
        this.property = property;
        this.allowlistTag = TagKey.of(RegistryKeys.BLOCK, Identifier.of(id.getNamespace(), ALLOWLIST_SUBDIRECTORY + "_" + id.getPath()));
        this.denylistTag = TagKey.of(RegistryKeys.BLOCK, Identifier.of(id.getNamespace(), DENYLIST_SUBDIRECTORY + "_" + id.getPath()));
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

    public boolean test(BlockState state) {
        return !state.isIn(this.getDenylistTag()) && state.isIn(this.getAllowlistTag()) && state.contains(property);
    }

    public BlockState applyDispenser(BlockState state, DispenserWrenchInteractionContext context) {
        if (this.test(state)) {
            Optional<T> result = this.applyDispenser(state.get(this.property), context);
            if (result.isPresent()) {
                return state.with(this.property, result.get());
            }
        }

        return state;
    }

    public BlockState applyManual(BlockState state, ManualWrenchInteractionContext context) {
        if (this.test(state)) {
            Optional<T> result = this.applyManual(state.get(this.property), context);
            if (result.isPresent()) {
                return state.with(this.property, result.get());
            }
        }

        return state;
    }

    protected abstract Optional<T> applyManual(T original, ManualWrenchInteractionContext context);

    protected abstract Optional<T> applyDispenser(T original, DispenserWrenchInteractionContext context);
}
