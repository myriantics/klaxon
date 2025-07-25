package net.myriantics.klaxon.mixin.entity_attributes;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.entry.RegistryEntry;
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
    private static DefaultAttributeContainer.Builder klaxon$appendKlaxonLivingAttributes(DefaultAttributeContainer.Builder original) {
        for (RegistryEntry<EntityAttribute> attribute : KlaxonEntityAttributes.getKlaxonGenericLivingEntityAttributes()) {
            original.add(attribute);
        }
        return original;
    }
}
