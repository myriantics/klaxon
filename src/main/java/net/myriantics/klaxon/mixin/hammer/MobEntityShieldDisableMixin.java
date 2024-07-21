package net.myriantics.klaxon.mixin.hammer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.item.tools.HammerItem;
import net.myriantics.klaxon.util.KlaxonTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityShieldDisableMixin {
    @WrapOperation(
            method = "tryAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;disablePlayerShield(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V")
    )
    private void shieldDisableWrapper(MobEntity instance, PlayerEntity player, ItemStack mobStack, ItemStack playerStack, Operation<Void> original) {
        if (!mobStack.isEmpty() && !playerStack.isEmpty() && mobStack.isIn(KlaxonTags.Items.SHEILD_DISABLING_MELEE) && playerStack.isOf(Items.SHIELD)) {
            player.getItemCooldownManager().set(playerStack.getItem(), 100);
            player.stopUsingItem();
            instance.getWorld().sendEntityStatus(player, (byte) 30);

        } else {
            original.call(instance, player, mobStack, playerStack);
        }
    }
}
