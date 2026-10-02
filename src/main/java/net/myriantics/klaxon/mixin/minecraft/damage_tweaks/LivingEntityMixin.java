package net.myriantics.klaxon.mixin.minecraft.damage_tweaks;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Shadow
    protected abstract void doHurtEquipment(DamageSource damageSource, float damageAmount, EquipmentSlot... slots);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "hurt",
            at = @At(value = "FIELD", target = "Lnet/minecraft/tags/DamageTypeTags;DAMAGES_HELMET:Lnet/minecraft/tags/TagKey;", opcode = Opcodes.GETSTATIC)
    )
    private void klaxon$handleBootsDamageMitigation(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) LocalFloatRef damageRef) {
        if (source.is(KlaxonDamageTypeTags.DAMAGES_BOOTS) && !this.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {
            this.doHurtEquipment(source, amount * 2, EquipmentSlot.FEET);
            damageRef.set(damageRef.get() * 0.25f);
        }
    }
}
