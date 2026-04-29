package net.myriantics.klaxon.block.functional.pressure_plate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.AABB;

import java.util.List;

public abstract class GatedPressurePlateBlock extends BasePressurePlateBlock {

    protected GatedPressurePlateBlock(Properties properties, BlockSetType type) {
        super(properties, type);
    }

    protected List<? extends Entity> getContactingEntities(Level level, Class<? extends Entity> clazz, AABB box) {
        return level.getEntitiesOfClass(clazz, box, this::baseEntityPredicate);
    }

    private boolean baseEntityPredicate(Entity entity) {
        return EntitySelector.NO_SPECTATORS.test(entity) && !entity.isIgnoringBlockTriggers() && entityPredicate(entity);
    }

    protected abstract boolean entityPredicate(Entity entity);

    @Override
    protected int getSignalStrength(Level level, BlockPos pos) {
        Class<? extends Entity> clazz = switch (this.type.pressurePlateSensitivity()) {
            case EVERYTHING -> Entity.class;
            case MOBS -> LivingEntity.class;
        };
        return !this.getContactingEntities(level, clazz, TOUCH_AABB.move(pos)).isEmpty() ? 15 : 0;
    }
}
