package net.myriantics.klaxon.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.item.tools.HammerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract World getEntityWorld();

    @Shadow public abstract EntityType<?> getType();

    @Inject(
            method = "getTargetingMargin",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void hammeringOverride(CallbackInfoReturnable<Float> cir) {
        // if it's the client, and it's an item, and the player can hammer the dropped item, increase the hitbox size
        if (getEntityWorld().isClient() && getType().equals(EntityType.ITEM)) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && player.isHolding(KlaxonItems.STEEL_HAMMER) && HammerItem.canProcessHammerRecipe(player)) {
                cir.setReturnValue(HammerItem.DROPPED_ITEM_INTERACTION_LEEWAY_BLOCKDISTANCE);
            }
        }
    }
}
