package net.myriantics.klaxon.mixin.blast_processor_behaviors;

import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WindChargeEntity.class)
public interface WindChargeEntityInvoker {

    @Invoker
    void invokeCreateExplosion(Vec3d pos);
}
