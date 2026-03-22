package net.myriantics.klaxon.mixin.minecraft.item_components.recipe_output_lore;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {

    @Inject(
            method = "getTooltipLines",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V", ordinal = 6)
    )
    private void klaxon$appendRecipeOutputData(
            Item.TooltipContext context,
            @Nullable Player player,
            TooltipFlag type,
            CallbackInfoReturnable<List<Component>> cir,
            @Local Consumer<Component> consumer
    ) {
        if (this.get(KlaxonDataComponentTypes.RECIPE_OUTPUT_CHANCE_LORE.value()) instanceof Double chance) {
            ChatFormatting color;
            if (chance >= 0.75) {
                color = ChatFormatting.GREEN;
            } else if (chance >= 0.5) {
                color = ChatFormatting.YELLOW;
            } else if (chance >= 0.25) {
                color = ChatFormatting.RED;
            } else {
                color = ChatFormatting.DARK_RED;
            }

            consumer.accept(
                    Component.translatable("klaxon.text.tooltip.recipe_output_lore.chance",
                            KlaxonMathHelper.roundToDecimalPlace(chance * 100, 4) + "%"
                    ).withStyle(color)
            );
        }
    }
}
