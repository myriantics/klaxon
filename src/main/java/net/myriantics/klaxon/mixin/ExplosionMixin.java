package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow public abstract RegistryEntry<SoundEvent> getSoundEvent();

    @Shadow @Final private ExplosionBehavior behavior;

    @WrapOperation(
            method = "collectBlocksAndDamageEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;emitGameEvent(Lnet/minecraft/entity/Entity;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/util/math/Vec3d;)V")
    )
    public void klaxon$muffledBlastProcessorOverride(World instance, Entity entity, RegistryEntry registryEntry, Vec3d vec3d, Operation<Void> original) {
        // if the explosion is from a muffled blast processor, bonk the explosion game event
        // this is so it doesnt proc sculk sensors
        // top 10 features people will totally make use of

        if (getSoundEvent().value().equals(SoundEvents.INTENTIONALLY_EMPTY)) {

            // don't emit explosion game event if it's a muffled blast processor explosion
            return;
        }
        original.call(instance, entity, registryEntry, vec3d);
    }
}
