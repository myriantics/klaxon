package net.myriantics.klaxon.mixin.minecraft.heavy_equipment;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.entity.KlaxonDataAttachments;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract Iterable<ItemStack> getEquippedItems();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;sendEquipmentChanges()V")
    )
    private void klaxon$updateHeavyEquipmentStatus(CallbackInfo ci) {
        this.getWorld().getProfiler().push("updateHeavyEquipmentStatus");

        boolean heavy = false;
        for (ItemStack stack : this.getEquippedItems()) {
            if (stack.isIn(KlaxonItemTags.HEAVY_EQUIPMENT)) {
                heavy = true;
                break;
            }
        }
        this.setAttached(KlaxonDataAttachments.HEAVY_EQUIPMENT, heavy);

        this.getWorld().getProfiler().pop();
    }
}
