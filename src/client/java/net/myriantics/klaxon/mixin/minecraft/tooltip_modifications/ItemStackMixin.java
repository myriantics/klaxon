package net.myriantics.klaxon.mixin.minecraft.tooltip_modifications;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.myriantics.klaxon.registry.item.KlaxonItemTooltipModifications;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(
            method = "getTooltipLines",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addAttributeTooltips(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/player/Player;)V")
    )
    public void klaxon$appendHeavyEquipmentText(
            Item.TooltipContext context,
            @Nullable Player player,
            TooltipFlag type,
            CallbackInfoReturnable<List<Component>> cir,
            @Local Consumer<Component> consumer
    ) {
        for (KlaxonItemTooltipModifications.TooltipModifier modifier : KlaxonItemTooltipModifications.MODIFIERS) {
            modifier.modify((ItemStack) (Object) this, context, player, type, consumer);
        }
    }
}
