package net.myriantics.klaxon.mixin.minecraft.crested_steel_helmet;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.passive.SnifferBrain;
import net.minecraft.entity.passive.SnifferEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SnifferBrain.class)
public interface SnifferBrainInvoker {
    @Invoker
    static void invokeAddDigActivities(Brain<SnifferEntity> brain) {
        throw new AssertionError();
    }
}
