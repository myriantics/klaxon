package net.myriantics.klaxon.mixin.minecraft.entity_attributes;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {
    @ModifyReturnValue(
            method = "createAttributes",
            at = @At(value = "RETURN")
    )
    private static AttributeSupplier.Builder klaxon$appendKlaxonPlayerEntityAttributes(AttributeSupplier.Builder original) {
        for (Holder<Attribute> entry : KlaxonEntityAttributes.getKlaxonPlayerEntityAttributes()) {
            original.add(entry);
        }

        return original;
    }
}
