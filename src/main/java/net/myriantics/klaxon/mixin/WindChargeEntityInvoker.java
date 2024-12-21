package net.myriantics.klaxon.mixin;

import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WindChargeEntity.class)
public interface WindChargeEntityInvoker {

    @Invoker
    void invokeCreateExplosion(Vec3d pos);

    @Accessor("EXPLOSION_BEHAVIOR")
    ExplosionBehavior klaxon$getExplosionBehavior();

    @Accessor("EXPLOSION_POWER")
    float klaxon$getExplosionPower();
}
