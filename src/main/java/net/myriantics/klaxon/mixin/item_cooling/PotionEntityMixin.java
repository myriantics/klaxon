package net.myriantics.klaxon.mixin.item_cooling;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.util.math.Box;
import net.myriantics.klaxon.recipe.cooling.ItemCoolingRecipeLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin {

    @Inject(
            method = "applyWater",
            at = @At(value = "TAIL")
    )
    public void klaxon$coolCoolableItemEntities(CallbackInfo ci, @Local Box checkingBox) {
        PotionEntity potionEntity = (PotionEntity) (Object) this;

        // run code for all coolable item entities within range
        for (ItemEntity itemEntity : potionEntity.getWorld().getEntitiesByClass(ItemEntity.class, checkingBox, (itemEntity -> ItemCoolingRecipeLogic.test(potionEntity.getWorld(), itemEntity.getStack())))) {
            itemEntity.extinguishWithSound();
        }
    }
}
