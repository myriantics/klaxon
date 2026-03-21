package net.myriantics.klaxon.mixin.minecraft.entity_attributes;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    // register KLAXON's custom entity attributes as tracked, so the game actually works :)
    @ModifyReturnValue(
            method = "createLivingAttributes",
            at = @At(value = "RETURN")
    )
    private static AttributeSupplier.Builder klaxon$appendKlaxonLivingAttributes(AttributeSupplier.Builder original) {
        for (Holder<Attribute> attribute : KlaxonEntityAttributes.getKlaxonGenericLivingEntityAttributes()) {
            original.add(attribute);
        }
        return original;
    }
}
