package net.myriantics.klaxon.mechanics.explosive_catalyst.context;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ExplosiveCatalystContext {
    private final ServerLevel level;
    private final DataComponentMap components;
    private final Map<ExplosiveCatalystContextParam<?>, Object> params = new HashMap<>();

    public ExplosiveCatalystContext(ServerLevel level, DataComponentMap components) {
        this.level = level;
        this.components = components;
    }

    public ServerLevel level() {
        return this.level;
    }

    public DataComponentMap components() {
        return this.components;
    }

    public <T> ExplosiveCatalystContext add(ExplosiveCatalystContextParam<T> param, T object) {
        this.params.put(param, object);
        return this;
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> T get(ExplosiveCatalystContextParam<T> param) {
        return (T) this.params.get(param);
    }
}
