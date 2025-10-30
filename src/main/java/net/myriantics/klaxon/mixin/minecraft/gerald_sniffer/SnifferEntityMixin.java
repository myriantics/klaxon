package net.myriantics.klaxon.mixin.minecraft.gerald_sniffer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.gerald_sniffer.GeraldSnifferHelper;
import net.myriantics.klaxon.mechanics.gerald_sniffer.GeraldSnifferState;
import net.myriantics.klaxon.mechanics.gerald_sniffer.SnifferEntityMixinAccess;
import net.myriantics.klaxon.registry.dynamic.KlaxonLootTables;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnifferEntity.class)
public abstract class SnifferEntityMixin extends AnimalEntity implements SnifferEntityMixinAccess {

    @Shadow
    public abstract boolean isDiggingOrSearching();

    @Unique
    private GeraldSnifferState klaxon$geraldSnifferState = GeraldSnifferState.TRACKING_UNSUPPORTED;

    protected SnifferEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
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
            GeraldSnifferHelper.lockOnToTrackingPos((SnifferEntity) (Object) this);
        }
    }

    @WrapOperation(
            method = "dropSeeds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/ReloadableRegistries$Lookup;getLootTable(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/loot/LootTable;")
    )
    private LootTable klaxon$overrideLootTableIfGeraldTrackingActive(
            ReloadableRegistries.Lookup instance,
            RegistryKey<LootTable> key,
            Operation<LootTable> original
    ) {
        return this.klaxon$geraldSnifferState.isActivelyTracking()
                ? original.call(instance, KlaxonLootTables.GERALD_SNIFFER_GAMEPLAY)
                : original.call(instance, key);
    }

    @WrapOperation(
            method = "dropSeeds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SnifferEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V")
    )
    private void klaxon$overrideSeedPickupSoundIfGeraldTrackingActive(
            SnifferEntity instance,
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
            method = "isDiggable",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z")
    )
    private boolean klaxon$canAlwaysDigIfGeraldTrackingActive(BlockState instance, TagKey<Block> tagKey, Operation<Boolean> original) {
        return this.klaxon$geraldSnifferState.isActivelyTracking() || original.call(instance, tagKey);
    }
}
