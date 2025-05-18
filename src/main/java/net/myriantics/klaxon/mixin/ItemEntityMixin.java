package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.ItemEntity;
import net.myriantics.klaxon.recipe.cooling.ItemCoolingHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @ModifyReturnValue(
            method = "isFireImmune",
            at = @At(value = "RETURN")
    )
    public boolean klaxon$coolableItemsAreImmuneToFire(boolean original) {
        ItemEntity self = (ItemEntity) (Object) this;

        return original || ItemCoolingHelper.test(self.getWorld(), self.getStack());
    }
}
