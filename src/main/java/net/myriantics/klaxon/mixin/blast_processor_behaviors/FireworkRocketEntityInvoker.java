package net.myriantics.klaxon.mixin.blast_processor_behaviors;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireworkRocketEntity.class)
public interface FireworkRocketEntityInvoker {

    // Used to explode fireworks in the corresponding BlastProcessorBehavior
    @Invoker(value = "explodeAndRemove")
    void invokeExplodeAndRemove();
}
