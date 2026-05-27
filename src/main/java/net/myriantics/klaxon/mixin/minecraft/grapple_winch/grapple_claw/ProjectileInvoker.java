package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Projectile.class)
public interface ProjectileInvoker {
    @Invoker("onHit")
    void klaxon$invokeOnHit(HitResult hitResult);
}
