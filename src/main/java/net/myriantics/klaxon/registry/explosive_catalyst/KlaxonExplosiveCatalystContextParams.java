package net.myriantics.klaxon.registry.explosive_catalyst;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContextParam;

public abstract class KlaxonExplosiveCatalystContextParams {
    public static final ExplosiveCatalystContextParam<BlockPos> BLOCK_POS = create("origin");
    public static final ExplosiveCatalystContextParam<Entity> SOURCE_ENTITY = create("source_entity");
    public static final ExplosiveCatalystContextParam<BlockState> SUPPORT_STATE = create("support_state");

    private static <T> ExplosiveCatalystContextParam<T> create(String name) {
        return new ExplosiveCatalystContextParam<>(KlaxonCommon.locate(name));
    }
}
