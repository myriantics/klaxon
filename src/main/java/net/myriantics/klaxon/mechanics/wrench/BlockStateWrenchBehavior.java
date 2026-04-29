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

    private static final WrenchInteractionMap EMPTY = WrenchInteractionMap.create();

    public BlockStateWrenchBehavior(
            Property<T> property,
            ResourceLocation id
    ) {
        this.property = property;
        this.allowlistTag = TagKey.create(Registries.BLOCK, id.withPath(path -> "blockstate_wrench_behavior/" + path + "/allowlist"));
        this.denylistTag = TagKey.create(Registries.BLOCK, id.withPath(path -> "blockstate_wrench_behavior/" + path + "/denylist"));
    }

    public final Property<T> getProperty() {
        return property;
    }

    public final TagKey<Block> getAllowlistTag() {
        return allowlistTag;
    }

    public final TagKey<Block> getDenylistTag() {
        return denylistTag;
    }

    public abstract WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context);

    public abstract WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context);

    public boolean test(BlockState state) {
        return state.hasProperty(this.property) && !state.is(this.getDenylistTag()) && state.is(this.getAllowlistTag());
    }
}
