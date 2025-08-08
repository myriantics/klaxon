package net.myriantics.klaxon.mixin.cable_shears;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.predicate.item.ItemPredicate;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(MatchToolLootCondition.class)
public abstract class MatchToolLootConditionMixin {
    @Shadow @Final private Optional<ItemPredicate> predicate;

    @ModifyExpressionValue(
            method = "test(Lnet/minecraft/loot/context/LootContext;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/predicate/item/ItemPredicate;test(Lnet/minecraft/item/ItemStack;)Z")
    )
    private boolean klaxon$testForCableShears(boolean original, @Local ItemStack stack) {
        if (!original && predicate.isPresent() && predicate.get().test(Items.SHEARS.getDefaultStack()) && stack.isOf(KlaxonItems.STEEL_CABLE_SHEARS)) {
            return true;
        }

        return original;
    }
}
