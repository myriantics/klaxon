package net.myriantics.klaxon.mixin.minecraft.hallnox_pod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(MushroomCow.class)
public abstract class MushroomCowMixin {
    @WrapOperation(
            method = "mobInteract",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/tags/ItemTags;SMALL_FLOWERS:Lnet/minecraft/tags/TagKey;", opcode = Opcodes.GETSTATIC),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/core/particles/ParticleTypes;SMOKE:Lnet/minecraft/core/particles/SimpleParticleType;", opcode = Opcodes.GETSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean klaxon$checkForHallnoxPod(ItemStack instance, TagKey<Item> tag, Operation<Boolean> original) {
        return original.call(instance, tag) || instance.is(KlaxonItems.HALLNOX_POD);
    }
}
