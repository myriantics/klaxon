package net.myriantics.klaxon.mixin.minecraft.entity_attributes;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @ModifyReturnValue(
            method = "createPlayerAttributes",
            at = @At(value = "RETURN")
    )
    private static DefaultAttributeContainer.Builder klaxon$appendKlaxonPlayerEntityAttributes(DefaultAttributeContainer.Builder original) {
        for (RegistryEntry<EntityAttribute> entry : KlaxonEntityAttributes.getKlaxonPlayerEntityAttributes()) {
            original.add(entry);
        }

        return original;
    }
}
