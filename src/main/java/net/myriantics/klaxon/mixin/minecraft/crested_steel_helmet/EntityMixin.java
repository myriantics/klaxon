package net.myriantics.klaxon.mixin.minecraft.crested_steel_helmet;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.SnifferBrain;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.crested_steel_helmet.CrestedSteelHelmetHelper;
import net.myriantics.klaxon.mechanics.crested_steel_helmet.SnifferEntityMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract World getWorld();

    @Inject(
            method = "setCustomName",
            at = @At(value = "HEAD")
    )
    private void klaxon$checkForGeraldSnifferRenameIfPossible(Text name, CallbackInfo ci) {
        if ((Object) this instanceof SnifferEntity snifferEntity && CrestedSteelHelmetHelper.validate(snifferEntity, name)) {
            Brain<SnifferEntity> brain = snifferEntity.getBrain();

            if (snifferEntity.canDig() && snifferEntity.canTryToDig()) {
                brain.remember(MemoryModuleType.SNIFFER_DIGGING, true);
            }

            brain.forget(MemoryModuleType.WALK_TARGET);
            brain.forget(MemoryModuleType.SNIFFER_SNIFFING_TARGET);

            ((SnifferEntityMixinAccess) snifferEntity).klaxon$setCrestedSteelHelmetTrackingStatus(true);
        }
    }
}
