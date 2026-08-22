package net.myriantics.klaxon.mixin.minecraft.ominous_deepslate_blast_processor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.BottleItem;
import net.myriantics.klaxon.entity.entities.mob.ominous_deepslate_blast_processor.OminousDeepslateBlastProcessorEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BottleItem.class)
public abstract class BottleItemMixin {
    @WrapMethod(
            method = "method_7726"
    )
    private static boolean klaxon$allowDragonsBreathPickupFromOminousDeepslateBlastProcessor(AreaEffectCloud areaEffectCloud, Operation<Boolean> original) {
        return original.call(areaEffectCloud) || (areaEffectCloud != null && areaEffectCloud.getOwner() instanceof OminousDeepslateBlastProcessorEntity);
    }
}
