package net.myriantics.klaxon.mixin.minecraft.storage;

import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HopperBlockEntity.class)
public interface HopperBlockEntityInvoker {
    @Invoker(value = "getEntityContainer")
    static Container klaxon$invokeGetEntityContainer(Level level, double x, double y, double z) {
        throw new AssertionError("Implemented via mixin");
    }
}
