package net.myriantics.klaxon.mixin.minecraft.item_components.recipe_output_lore;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.ComponentHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
public abstract class ItemStackMixin implements ComponentHolder {

    @Inject(
            method = "getTooltip",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V", ordinal = 6)
    )
    private void klaxon$appendRecipeOutputData(
            Item.TooltipContext context,
            @Nullable PlayerEntity player,
            TooltipType type,
            CallbackInfoReturnable<List<Text>> cir,
            @Local Consumer<Text> consumer
    ) {
        if (this.get(KlaxonDataComponentTypes.RECIPE_OUTPUT_CHANCE_LORE) instanceof Double chance) {
            Formatting color;
            if (chance >= 0.75) {
                color = Formatting.GREEN;
            } else if (chance >= 0.5) {
                color = Formatting.YELLOW;
            } else if (chance >= 0.25) {
                color = Formatting.RED;
            } else {
                color = Formatting.DARK_RED;
            }

            consumer.accept(
                    Text.translatable("klaxon.text.tooltip.recipe_output_lore.chance",
                            KlaxonMathHelper.roundToDecimalPlace(chance * 100, 4) + "%"
                    ).formatted(color)
            );
        }
    }
}
