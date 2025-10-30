package net.myriantics.klaxon.mixin.minecraft.gerald_sniffer;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.NbtCompound;
import net.myriantics.klaxon.mechanics.gerald_sniffer.GeraldSnifferState;
import net.myriantics.klaxon.mechanics.gerald_sniffer.SnifferEntityMixinAccess;
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
            nbt.putString(
                    KlaxonNBTIds.GERALD_SNIFFER_STATE,
                    access.klaxon$getGeraldSnifferState().asString()
            );
        }
    }

    @Inject(
            method = "readCustomDataFromNbt",
            at = @At(value = "TAIL")
    )
    private void klaxon$readCrestedSteelHelmetTrackingStatus(NbtCompound nbt, CallbackInfo ci) {
        if ((Object) this instanceof SnifferEntityMixinAccess access && nbt.contains(KlaxonNBTIds.GERALD_SNIFFER_STATE)) {
            access.klaxon$setGeraldSnifferState(
                    GeraldSnifferState.fromString(nbt.getString(KlaxonNBTIds.GERALD_SNIFFER_STATE))
            );
        }
    }
}
