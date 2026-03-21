package net.myriantics.klaxon.mixin.minecraft.gerald_sniffer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.myriantics.klaxon.mechanics.gerald_sniffer.GeraldSnifferHelper;
import net.myriantics.klaxon.mechanics.gerald_sniffer.GeraldSnifferState;
import net.myriantics.klaxon.mechanics.gerald_sniffer.SnifferEntityMixinAccess;
import net.myriantics.klaxon.registry.dynamic.KlaxonLootTables;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Sniffer.class)
public abstract class SnifferEntityMixin extends Animal implements SnifferEntityMixinAccess {

    @Unique
    private GeraldSnifferState klaxon$geraldSnifferState = GeraldSnifferState.TRACKING_UNSUPPORTED;

    protected SnifferEntityMixin(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void klaxon$setGeraldSnifferState(GeraldSnifferState newState) {
        this.klaxon$geraldSnifferState = newState;
    }

    @Override
    public GeraldSnifferState klaxon$getGeraldSnifferState() {
        return this.klaxon$geraldSnifferState;
    }

    @Inject(
            method = "tick",
            at = @At(value = "TAIL")
    )
    private void klaxon$lockOnToGeraldTargetIfGeraldTrackingIsActive(CallbackInfo ci) {
        if (this.klaxon$geraldSnifferState.isReadyToStartTracking()) {
            GeraldSnifferHelper.lockOnToTrackingPos((Sniffer) (Object) this);
        }
    }

    @WrapOperation(
            method = "dropSeed",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;")
    )
    private LootTable klaxon$overrideLootTableIfGeraldTrackingActive(
            ReloadableServerRegistries.Holder instance,
            ResourceKey<LootTable> key,
            Operation<LootTable> original
    ) {
        return this.klaxon$geraldSnifferState.isActivelyTracking()
                ? original.call(instance, KlaxonLootTables.GERALD_SNIFFER_GAMEPLAY)
                : original.call(instance, key);
    }

    @WrapOperation(
            method = "dropSeed",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/sniffer/Sniffer;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V")
    )
    private void klaxon$overrideSeedPickupSoundIfGeraldTrackingActive(
            Sniffer instance,
            SoundEvent soundEvent,
            float v, float p,
            Operation<Void> original
    ) {
        if (this.klaxon$geraldSnifferState.isActivelyTracking()) {
            original.call(instance, KlaxonSoundEvents.SNIFFER_DIG_METAL, v, p);
            this.klaxon$setGeraldSnifferState(GeraldSnifferState.TRACKING_FINISHED);
        } else {
            original.call(instance, soundEvent, v, p);
        }
    }

    @WrapOperation(
            method = "canDig(Lnet/minecraft/core/BlockPos;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean klaxon$canAlwaysDigIfGeraldTrackingActive(BlockState instance, TagKey<Block> tagKey, Operation<Boolean> original) {
        return !this.klaxon$geraldSnifferState.equals(GeraldSnifferState.TRACKING_UNSUPPORTED) || original.call(instance, tagKey);
    }
}
