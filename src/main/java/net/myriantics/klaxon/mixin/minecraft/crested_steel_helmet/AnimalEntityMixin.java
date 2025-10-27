package net.myriantics.klaxon.mixin.minecraft.crested_steel_helmet;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.nbt.NbtCompound;
import net.myriantics.klaxon.mechanics.crested_steel_helmet.SnifferEntityMixinAccess;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin {
    @Inject(
            method = "writeCustomDataToNbt",
            at = @At(value = "TAIL")
    )
    private void klaxon$writeCrestedSteelHelmetTrackingStatus(NbtCompound nbt, CallbackInfo ci) {
        if ((Object) this instanceof SnifferEntityMixinAccess access) {
            nbt.putBoolean(
                    KlaxonNBTIds.IS_TRACKING_CRESTED_STEEL_HELMET,
                    access.klaxon$isTrackingCrestedSteelHelmet()
            );
        }
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At(value = "TAIL")
    )
    private void klaxon$readCrestedSteelHelmetTrackingStatus(NbtCompound nbt, CallbackInfo ci) {
        if ((Object) this instanceof SnifferEntityMixinAccess access) {
            access.klaxon$setCrestedSteelHelmetTrackingStatus(access.klaxon$isTrackingCrestedSteelHelmet());
        }
    }
}
