package net.myriantics.klaxon.mixin.minecraft.blast_processor_behaviors;

import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WindCharge.class)
public interface WindChargeEntityInvoker {

    @Invoker
    void invokeExplode(Vec3 pos);
}
