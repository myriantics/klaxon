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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityShieldDisableMixin {
    // this ensures player's shield is still broken while attack is carried out
    // this is not very elegant but ehh ill clean it up later
    @ModifyExpressionValue(
            method = "disablePlayerShield",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;")
    )
    private Item checkForHammer(Item original, @Local(argsOnly = true) PlayerEntity player) {
        if (original instanceof HammerItem) {
            player.sendMessage(Text.literal("test 1"));
            return Items.NETHERITE_AXE;
        }
        return original;
    }

    @WrapOperation(
            method = "tryAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;disablePlayerShield(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V")
    )
    private void hammerShieldCondition(MobEntity instance, PlayerEntity player, ItemStack mobStack, ItemStack playerStack, Operation<Void> original) {
        if (mobStack.isOf(KlaxonItems.HAMMER)) {

        }

        player.sendMessage(Text.literal("test 2"));
        player.disableShield(player.isSprinting());
    }

    //private void hammerShieldDisable
}
