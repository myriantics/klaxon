package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.advancement.KlaxonAdvancementCriteria;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {

    @Shadow @Final private Property levelCost;

    @ModifyExpressionValue(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;canHaveEnchantments(Lnet/minecraft/item/ItemStack;)Z")
    )
    public boolean klaxon$unenchantableOverride(boolean original, @Local ItemStack inputStack) {
        // this way anvils are still allowed to be used but anything else that calls the canHaveEnchantments methods get denied
        return original || inputStack.isIn(KlaxonItemTags.UNENCHANTABLE);
    }

    @ModifyExpressionValue(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamageable()Z", ordinal = 1)
    )
    public boolean klaxon$repairOverride(boolean original, @Local(ordinal = 0) ItemStack inputStack) {
        // doesnt allow enchants to be applied to unenchantable items
        return original || !inputStack.isIn(KlaxonItemTags.UNENCHANTABLE);
    }

    // halves durability gained from using makeshift repair materials to repair items

    @ModifyExpressionValue(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I", ordinal = 0)
    )
    public int klaxon$makeshiftRepairMaterialOverride1(int original, @Local(ordinal = 2) ItemStack repairItem) {

        return repairItem.isIn(KlaxonItemTags.MAKESHIFT_REPAIR_MATERIALS) ? original / 2 : original;
    }

    @ModifyExpressionValue(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I", ordinal = 1)
    )
    public int klaxon$makeshiftRepairMaterialOverride2(int original, @Local(ordinal = 2) ItemStack repairItem) {

        return repairItem.isIn(KlaxonItemTags.MAKESHIFT_REPAIR_MATERIALS) ? original / 2 : original;
    }

    @Inject(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;sendContentUpdates()V")
    )
    public void klaxon$applyFreeRepairExpDiscount(CallbackInfo ci, @Local(ordinal = 0) ItemStack repairedStack) {
        // makes an item free to repair if it's in the tag
        if (repairedStack.isIn(KlaxonItemTags.NO_XP_COST_REPAIRABLE)) {
            levelCost.set(0);
        }
    }

    @ModifyExpressionValue(
            method = "canTakeOutput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I")
    )
    public int klaxon$canTakeItemOverride(int original, @Local(argsOnly = true) boolean present) {
        // jank method of allowing items to be removed if they cost 0 levels
        if (original == 0 && present) {
            return 1;
        }
        return original;
    }

    @WrapOperation(
            method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;set(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 1)
    )
    private <T> T klaxon$anvilUsesOverride(ItemStack instance, ComponentType<? super T> type, @Nullable T value, Operation<T> original) {
        // crappy way of overwriting anvil uses
        if (instance.isIn(KlaxonItemTags.INFINITELY_REPAIRABLE)) {
            return (T) Integer.valueOf(0);
        }
        return original.call(instance, type, value);
    }

    @Inject(
            method = "onTakeOutput",
            at = @At(value = "HEAD")
    )
    public void klaxon$repairAdvancementHook(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            KlaxonAdvancementCriteria.ANVIL_REPAIR_CRITERION.trigger(serverPlayer, stack);
        }
    }
}
