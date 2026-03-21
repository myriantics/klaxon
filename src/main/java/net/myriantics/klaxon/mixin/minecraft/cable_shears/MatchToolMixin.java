package net.myriantics.klaxon.mixin.minecraft.cable_shears;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(MatchTool.class)
public abstract class MatchToolMixin {
    @Shadow @Final private Optional<ItemPredicate> predicate;

    @ModifyExpressionValue(
            method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/ItemPredicate;test(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private boolean klaxon$testForCableShears(boolean original, @Local ItemStack stack) {
        if (!original && predicate.isPresent() && predicate.get().test(Items.SHEARS.getDefaultInstance()) && stack.is(KlaxonItems.STEEL_CABLE_SHEARS)) {
            return true;
        }

        return original;
    }
}
