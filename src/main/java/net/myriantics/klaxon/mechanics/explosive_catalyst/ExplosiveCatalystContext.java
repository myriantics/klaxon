package net.myriantics.klaxon.mechanics.explosive_catalyst;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public sealed abstract class ExplosiveCatalystContext permits ExplosiveCatalystContext.Block, ExplosiveCatalystContext.Item, ExplosiveCatalystContext.Entity {
    private final Level level;
    private final DataComponentMap components;

    protected ExplosiveCatalystContext(Level level, DataComponentMap components) {
        this.level = level;
        this.components = components;
    }

    public Level level() {
        return this.level;
    }

    public DataComponentMap components() {
        return this.components;
    }

    public @Nullable net.minecraft.world.entity.Entity getEntity() {
        return null;
    }

    public static final class Block extends ExplosiveCatalystContext {
        private final BlockState state;
        private final BlockPos pos;

        public Block(Level level, DataComponentMap components, BlockPos pos) {
            super(level, components);
            this.pos = pos;
            this.state = level.getBlockState(pos);
        }

        public BlockState getState() {
            return state;
        }

        public BlockPos getPos() {
            return pos;
        }
    }

    public static final class Item extends ExplosiveCatalystContext {

        Item(Level level, DataComponentMap components) {
            super(level, components);
        }
    }

    public static final class Entity extends ExplosiveCatalystContext {

        private final net.minecraft.world.entity.Entity entity;

        Entity(Level level, net.minecraft.world.entity.Entity entity, DataComponentMap components) {
            super(level, components);
            this.entity = entity;
        }

        @Override
        public @Nullable net.minecraft.world.entity.Entity getEntity() {
            return this.entity;
        }
    }
}
