package net.myriantics.klaxon.mixin.minecraft.crested_steel_helmet;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.mechanics.crested_steel_helmet.CrestedSteelHelmetHelper;
import net.myriantics.klaxon.mechanics.crested_steel_helmet.SnifferEntityMixinAccess;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnifferEntity.class)
public abstract class SnifferEntityMixin extends AnimalEntity implements SnifferEntityMixinAccess {

    @Shadow
    protected abstract SnifferEntity.State getState();

    @Unique
    private boolean klaxon$isTrackingCrestedSteelHelmet = false;

    protected SnifferEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void klaxon$setCrestedSteelHelmetTrackingStatus(boolean tracking) {
        this.klaxon$isTrackingCrestedSteelHelmet = tracking;
    }

    @Override
    public boolean klaxon$isTrackingCrestedSteelHelmet() {
        return this.klaxon$isTrackingCrestedSteelHelmet;
    }

    @Inject(
            method = "startState",
            at = @At(value = "HEAD")
    )
    private void klaxon$resetCrestedSteelHelmetTracking(SnifferEntity.State state, CallbackInfoReturnable<SnifferEntity> cir) {
        if (this.klaxon$isTrackingCrestedSteelHelmet && this.getState().equals(SnifferEntity.State.DIGGING)) {
            this.emitGameEvent(GameEvent.ENTITY_ACTION);
            this.discard();
        }
    }

    @WrapOperation(
            method = "dropSeeds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/ReloadableRegistries$Lookup;getLootTable(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/loot/LootTable;")
    )
    private LootTable klaxon$overrideLootTableIfTrackingCrestedSteelHelmet(ReloadableRegistries.Lookup instance, RegistryKey<LootTable> key, Operation<LootTable> original) {
        return this.klaxon$isTrackingCrestedSteelHelmet
                ? original.call(instance, CrestedSteelHelmetHelper.SNIFFER_DIGGING_CRESTED_STEEL_HELMET_GAMEPLAY)
                : original.call(instance, key);
    }

    @WrapOperation(
            method = "dropSeeds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SnifferEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V")
    )
    private void klaxon$overrideSeedPickupSound(SnifferEntity instance, SoundEvent soundEvent, float v, float p, Operation<Void> original) {
        if (this.klaxon$isTrackingCrestedSteelHelmet) {
            original.call(instance, KlaxonSoundEvents.SNIFFER_DIG_METAL, v, p);
        } else {
            original.call(instance, soundEvent, v, p);
        }
    }
}
