package net.myriantics.klaxon.mixin.minecraft.gerald_sniffer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.Animal;
import net.myriantics.klaxon.mechanics.gerald_sniffer.GeraldSnifferState;
import net.myriantics.klaxon.mechanics.gerald_sniffer.SnifferEntityMixinAccess;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin {
    @Inject(
            method = "addAdditionalSaveData",
            at = @At(value = "TAIL")
    )
    private void klaxon$writeCrestedSteelHelmetTrackingStatus(CompoundTag nbt, CallbackInfo ci) {
        if ((Object) this instanceof SnifferEntityMixinAccess access) {
            nbt.putString(
                    KlaxonNBTIds.GERALD_SNIFFER_STATE,
                    access.klaxon$getGeraldSnifferState().getSerializedName()
            );
        }
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At(value = "TAIL")
    )
    private void klaxon$readCrestedSteelHelmetTrackingStatus(CompoundTag nbt, CallbackInfo ci) {
        if ((Object) this instanceof SnifferEntityMixinAccess access && nbt.contains(KlaxonNBTIds.GERALD_SNIFFER_STATE)) {
            access.klaxon$setGeraldSnifferState(
                    GeraldSnifferState.fromString(nbt.getString(KlaxonNBTIds.GERALD_SNIFFER_STATE))
            );
        }
    }
}
